package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.payload.request.PostShippingReturnDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.payload.request.PostCompletedReturnRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.request.GhnShippingFeeRequest;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.MBBankPaymentInfoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    @Autowired
    private ProductDiscountService productDiscountService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter REVERT_REASON_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Value("${mbbank.bank-code:MB}")
    private String mbBankCode;

    @Value("${mbbank.bank-name:MB Bank}")
    private String mbBankName;

    @Value("${mbbank.account-number:}")
    private String mbBankAccountNumber;

    @Value("${mbbank.account-name:}")
    private String mbBankAccountName;

    @Value("${mbbank.vietqr.base-url:https://img.vietqr.io/image}")
    private String mbBankVietQrBaseUrl;

    @Value("${mbbank.vietqr.bin:970422}")
    private String mbBankVietQrBin;

    @Value("${mbbank.vietqr.template:compact2}")
    private String mbBankVietQrTemplate;

    @Value("${ghn.api.base-url:https://online-gateway.ghn.vn/shiip/public-api}")
    private String ghnApiBaseUrl;

    @Value("${ghn.token:}")
    private String ghnToken;

    @Value("${ghn.shop-id:0}")
    private Integer ghnShopId;

    @Value("${ghn.from-district-id:0}")
    private Integer ghnFromDistrictId;

    @Value("${ghn.service-type-id:2}")
    private Integer ghnServiceTypeId;

    @Value("${ghn.default-weight:1000}")
    private Integer ghnDefaultWeight;

    @Value("${ghn.default-length:20}")
    private Integer ghnDefaultLength;

    @Value("${ghn.default-width:20}")
    private Integer ghnDefaultWidth;

    @Value("${ghn.default-height:10}")
    private Integer ghnDefaultHeight;

    @Transactional
    public Order checkoutCOD(Integer accountId) {
        return checkout(accountId, "COD");
    }

    @Transactional
    public Order checkout(Integer accountId, String method) {
        return checkout(accountId, method, null, null);
    }

    @Transactional
    public Order checkout(Integer accountId, String method, List<Integer> selectedCartItemIds) {
        return checkout(accountId, method, selectedCartItemIds, null, null);
    }

    @Transactional
    public Order checkout(Integer accountId, String method, List<Integer> selectedCartItemIds, String couponCode) {
        return checkout(accountId, method, selectedCartItemIds, couponCode, null);
    }

    @Transactional
    public Order checkout(Integer accountId, String method, List<Integer> selectedCartItemIds, String couponCode, BigDecimal shippingFee) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        Cart cart = cartRepository.findByAccountID_Id(accountId)
                .orElse(null);
        if (cart == null) {
            throw new RuntimeException("Không tìm thấy giỏ hàng");
        }
        List<CartItem> allCartItems =
                cartItemRepository.findByCartID_Id(cart.getId());

        if (allCartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        List<CartItem> cartItems;
        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            cartItems = allCartItems;
        } else {
            Set<Integer> selectedSet = new HashSet<>(selectedCartItemIds);
            cartItems = allCartItems.stream()
                    .filter(item -> selectedSet.contains(item.getId()))
                    .toList();

            if (cartItems.size() != selectedSet.size()) {
                throw new RuntimeException("Có sản phẩm không thuộc giỏ hàng hiện tại");
            }
        }

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 sản phẩm để thanh toán");
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        Order order = new Order();
        order.setAccountID(account);
        order.setOrderType("ONLINE");
        order.setStatus("PENDING_PAYMENT");
        Address address = addressRepository.findTopByAccount_IdOrderByIdDesc(accountId);
        order.setShippingAddress(formatAddressSnapshot(address));
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            ProductColor productColor = item.getProductColorID();
            if (productColor == null) {
                throw new RuntimeException("Sản phẩm không hợp lệ trong giỏ hàng");
            }

            Integer quantity = item.getQuantity();
            if (quantity == null || quantity <= 0) {
                throw new RuntimeException("Số lượng sản phẩm không hợp lệ");
            }

            Integer currentStock = productColor.getStockQuantity() == null ? 0 : productColor.getStockQuantity();
            if (currentStock < quantity) {
                String productName = productColor.getProductID() != null
                        ? productColor.getProductID().getProductName()
                        : "Sản phẩm";
                throw new RuntimeException(productName + " không đủ tồn kho");
            }

            productColor.setStockQuantity(currentStock - quantity);
            productColorRepository.save(productColor);

            BigDecimal price = productDiscountService.getDiscountedPrice(productColor);
            subTotal = subTotal.add(
                    price.multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            OrderDetail detail = new OrderDetail();
            detail.setOrderID(order);
            detail.setProductColorID(item.getProductColorID());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(price);
            orderDetailRepository.save(detail);
        }

        DiscountCoupon appliedCoupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            appliedCoupon = validateCoupon(couponCode.trim(), subTotal);
            discountAmount = calculateDiscountAmount(appliedCoupon, subTotal);
            Integer currentQuantity = appliedCoupon.getQuantity() == null ? 0 : appliedCoupon.getQuantity();
            appliedCoupon.setQuantity(Math.max(currentQuantity - 1, 0));
            discountCouponRepository.save(appliedCoupon);
            order.setCouponID(appliedCoupon);
        }

        BigDecimal safeShippingFee = shippingFee == null ? BigDecimal.ZERO : shippingFee.max(BigDecimal.ZERO);
        BigDecimal total = subTotal.subtract(discountAmount).max(BigDecimal.ZERO).add(safeShippingFee);
        order.setTotalAmount(total);
        orderRepository.save(order);

        String normalizedMethod = normalizePaymentMethod(method);
        if (!List.of("COD", "BANK_TRANSFER", "E_WALLET").contains(normalizedMethod)) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ");
        }

        Payment payment = new Payment();
        payment.setOrderID(order);
        payment.setAmount(total);
        payment.setMethod(normalizedMethod);
        payment.setStatus("UNPAID");
        paymentRepository.save(payment);

        cartItemRepository.deleteAll(cartItems);
        return order;
    }

    public BigDecimal calculateShippingFeeByGhn(GhnShippingFeeRequest request) {
        if (request == null) {
            throw new RuntimeException("Thiếu dữ liệu tính phí GHN");
        }

        String token = safeTrim(ghnToken);
        if (token.isEmpty() || ghnShopId == null || ghnShopId <= 0 || ghnFromDistrictId == null || ghnFromDistrictId <= 0) {
            throw new RuntimeException("GHN chưa được cấu hình đầy đủ (token/shop-id/from-district-id)");
        }

        Integer toDistrictId = request.getToDistrictId();
        String toWardCode = safeTrim(request.getToWardCode());
        if (toDistrictId == null || toDistrictId <= 0) {
            throw new RuntimeException("Thiếu mã quận/huyện GHN (toDistrictId)");
        }
        if (toWardCode.isEmpty()) {
            throw new RuntimeException("Thiếu mã phường/xã GHN (toWardCode)");
        }

        int weight = normalizePositive(request.getWeight(), ghnDefaultWeight, 1000);
        int length = normalizePositive(request.getLength(), ghnDefaultLength, 20);
        int width = normalizePositive(request.getWidth(), ghnDefaultWidth, 20);
        int height = normalizePositive(request.getHeight(), ghnDefaultHeight, 10);
        long insuranceValue = (request.getInsuranceValue() == null ? BigDecimal.ZERO : request.getInsuranceValue().max(BigDecimal.ZERO))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        String endpoint = trimTrailingSlash(safeTrim(ghnApiBaseUrl)) + "/v2/shipping-order/fee";

        try {
            ObjectNode bodyNode = objectMapper.createObjectNode();
            bodyNode.put("service_type_id", normalizePositive(ghnServiceTypeId, 2, 2));
            bodyNode.put("from_district_id", ghnFromDistrictId);
            bodyNode.put("to_district_id", toDistrictId);
            bodyNode.put("to_ward_code", toWardCode);
            bodyNode.put("height", height);
            bodyNode.put("length", length);
            bodyNode.put("weight", weight);
            bodyNode.put("width", width);
            bodyNode.put("insurance_value", insuranceValue);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Token", token)
                    .header("ShopId", String.valueOf(ghnShopId))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(bodyNode)))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode rootNode = objectMapper.readTree(response.body());
            int code = rootNode.path("code").asInt(-1);
            if (response.statusCode() >= 400 || code != 200) {
                String message = rootNode.path("message").asText("GHN không thể tính phí vận chuyển");
                throw new RuntimeException(message);
            }

            long totalFee = rootNode.path("data").path("total").asLong(-1);
            if (totalFee < 0) {
                throw new RuntimeException("GHN trả về phí vận chuyển không hợp lệ");
            }

            return BigDecimal.valueOf(totalFee);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Yêu cầu tính phí GHN bị gián đoạn", ex);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tính phí vận chuyển GHN", ex);
        }
    }

    public List<Map<String, Object>> getGhnProvinces() {
        JsonNode rootNode = callGhnMasterData("/master-data/province", null);
        JsonNode dataNode = rootNode.path("data");
        List<Map<String, Object>> provinces = new ArrayList<>();

        if (dataNode.isArray()) {
            for (JsonNode provinceNode : dataNode) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("provinceId", provinceNode.path("ProvinceID").asInt(0));
                item.put("provinceName", provinceNode.path("ProvinceName").asText(""));
                provinces.add(item);
            }
        }

        return provinces;
    }

    public List<Map<String, Object>> getGhnDistricts(Integer provinceId) {
        if (provinceId == null || provinceId <= 0) {
            throw new RuntimeException("Thiếu provinceId GHN hợp lệ");
        }

        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.put("province_id", provinceId);
        JsonNode rootNode = callGhnMasterData("/master-data/district", bodyNode);
        JsonNode dataNode = rootNode.path("data");
        List<Map<String, Object>> districts = new ArrayList<>();

        if (dataNode.isArray()) {
            for (JsonNode districtNode : dataNode) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("districtId", districtNode.path("DistrictID").asInt(0));
                item.put("districtName", districtNode.path("DistrictName").asText(""));
                districts.add(item);
            }
        }

        return districts;
    }

    public List<Map<String, Object>> getGhnWards(Integer districtId) {
        if (districtId == null || districtId <= 0) {
            throw new RuntimeException("Thiếu districtId GHN hợp lệ");
        }

        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.put("district_id", districtId);
        JsonNode rootNode = callGhnMasterData("/master-data/ward", bodyNode);
        JsonNode dataNode = rootNode.path("data");
        List<Map<String, Object>> wards = new ArrayList<>();

        if (dataNode.isArray()) {
            for (JsonNode wardNode : dataNode) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("wardCode", wardNode.path("WardCode").asText(""));
                item.put("wardName", wardNode.path("WardName").asText(""));
                wards.add(item);
            }
        }

        return wards;
    }

    public List<GetPaidOrderWithDetailsDto> getOrdersWithDetailsForAccount(Integer accountId) {
        List<Order> orders = orderRepository.findByAccountID_Id(accountId);
        return orders.stream()
                .map(this::mapOrderToDetailsDto)
                .toList();
    }

    public List<GetPaidOrderWithDetailsDto> getAllOrdersWithDetails() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapOrderToDetailsDto)
                .toList();
    }

    public MBBankPaymentInfoResponse getMBBankPaymentInfo(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán của đơn hàng"));

        String paymentMethod = normalizePaymentMethod(payment.getMethod());
        if (!"BANK_TRANSFER".equals(paymentMethod)) {
            throw new RuntimeException("Đơn hàng này không sử dụng phương thức chuyển khoản ngân hàng");
        }

        String accountNo = safeTrim(mbBankAccountNumber);
        String accountName = safeTrim(mbBankAccountName);
        if (accountNo.isEmpty() || accountName.isEmpty()) {
            throw new RuntimeException("MB Bank chưa được cấu hình đầy đủ trên hệ thống");
        }

        BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : order.getTotalAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        String transferContent = "DH" + order.getId();

        MBBankPaymentInfoResponse response = new MBBankPaymentInfoResponse();
        response.setOrderId(order.getId());
        response.setAmount(amount);
        response.setBankCode(safeTrim(mbBankCode));
        response.setBankName(safeTrim(mbBankName));
        response.setAccountNumber(accountNo);
        response.setAccountName(accountName);
        response.setTransferContent(transferContent);
        response.setQrUrl(buildVietQrUrl(amount, transferContent, accountName, accountNo));
        return response;
    }

    @Transactional
    public Order confirmPayment(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán của đơn hàng"));

        String currentOrderStatus = normalizeOrderStatus(order.getStatus());
        String currentPaymentStatus = normalizePaymentStatus(payment.getStatus());

        if ("CANCELLED".equals(currentOrderStatus) || "CANCELLED".equals(currentPaymentStatus)) {
            throw new RuntimeException("Không thể xác nhận thanh toán cho đơn đã hủy");
        }

        if ("PAID".equals(currentOrderStatus) || "PAID".equals(currentPaymentStatus)) {
            throw new RuntimeException("Đơn hàng đã được thanh toán trước đó");
        }

        payment.setStatus("PAID");
        paymentRepository.save(payment);

        String orderType = order.getOrderType() == null
                ? ""
                : order.getOrderType().trim().toUpperCase(Locale.ROOT);

        if ("OFFLINE".equals(orderType)) {
            order.setStatus("PAID");
        } else {
            order.setStatus("SHIPPING");
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrderForAccount(Integer accountId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getAccountID() == null || !order.getAccountID().getId().equals(accountId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }

        return cancelOrderInternal(order, true);
    }

    @Transactional
    public Order cancelOrderByAdmin(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return cancelOrderInternal(order, false);
    }

    @Transactional
    public Order revertOrderStatusByAdmin(Integer orderId, String reason) {
        String normalizedReason = reason == null ? "" : reason.trim();
        if (normalizedReason.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do khi quay lại trạng thái");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        appendRevertReason(order, normalizedReason);

        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order).orElse(null);

        String currentOrderStatus = normalizeOrderStatus(order.getStatus());
        String currentPaymentStatus = payment != null
                ? normalizePaymentStatus(payment.getStatus())
                : "UNKNOWN";

        boolean isPending = "PENDING_PAYMENT".equals(currentOrderStatus)
                && ("UNPAID".equals(currentPaymentStatus) || "UNKNOWN".equals(currentPaymentStatus));
        if (isPending) {
            throw new RuntimeException("Đơn hàng đang ở trạng thái ban đầu, không thể quay lại thêm");
        }

        if ("PAID".equals(currentOrderStatus) || "PAID".equals(currentPaymentStatus)) {
            String paymentMethod = payment != null
                    ? normalizePaymentMethod(payment.getMethod())
                    : "";

            if ("COD".equals(paymentMethod)
                    && ("SHIPPING".equals(currentOrderStatus) || "PAID".equals(currentOrderStatus))) {
                if (payment != null) {
                    payment.setStatus("UNPAID");
                    paymentRepository.save(payment);
                }
                order.setStatus("SHIPPING");
                orderRepository.save(order);
                return order;
            }

            if (!"COD".equals(paymentMethod)) {
                if (payment != null && !"PAID".equals(currentPaymentStatus)) {
                    payment.setStatus("PAID");
                    paymentRepository.save(payment);
                }
                order.setStatus("SHIPPING");
                orderRepository.save(order);
                return order;
            }

            if (payment != null) {
                payment.setStatus("UNPAID");
                paymentRepository.save(payment);
            }
            order.setStatus("PENDING_PAYMENT");
            orderRepository.save(order);
            return order;
        }

        if ("SHIPPING".equals(currentOrderStatus)) {
            order.setStatus("PENDING_PAYMENT");
            orderRepository.save(order);
            return order;
        }

        if ("CANCELLED".equals(currentOrderStatus) || "CANCELLED".equals(currentPaymentStatus)) {
            List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(order.getId());
            for (OrderDetail detail : details) {
                ProductColor productColor = detail.getProductColorID();
                if (productColor == null) {
                    continue;
                }

                Integer currentStock = productColor.getStockQuantity() == null ? 0 : productColor.getStockQuantity();
                Integer quantity = detail.getQuantity() == null ? 0 : detail.getQuantity();

                if (currentStock < quantity) {
                    String productName = productColor.getProductID() != null
                            ? productColor.getProductID().getProductName()
                            : "Sản phẩm";
                    throw new RuntimeException("Không đủ tồn kho để khôi phục đơn: " + productName);
                }

                productColor.setStockQuantity(currentStock - quantity);
                productColorRepository.save(productColor);
            }

            DiscountCoupon coupon = order.getCouponID();
            if (coupon != null) {
                Integer currentQuantity = coupon.getQuantity() == null ? 0 : coupon.getQuantity();
                if (currentQuantity <= 0) {
                    throw new RuntimeException("Không đủ lượt mã giảm giá để khôi phục đơn");
                }
                coupon.setQuantity(currentQuantity - 1);
                discountCouponRepository.save(coupon);
            }

            if (payment != null) {
                payment.setStatus("UNPAID");
                paymentRepository.save(payment);
            }
            order.setStatus("PENDING_PAYMENT");
            orderRepository.save(order);
            return order;
        }

        throw new RuntimeException("Trạng thái hiện tại không hỗ trợ quay lại");
    }

    private void appendRevertReason(Order order, String reason) {
        String currentNote = order.getNote() == null ? "" : order.getNote().trim();
        String timeText = LocalDateTime.now().format(REVERT_REASON_TIME_FORMAT);
        String line = "[REVERT " + timeText + "] " + reason;

        if (currentNote.isEmpty()) {
            order.setNote(line);
            return;
        }

        String combined = currentNote + "\n" + line;
        if (combined.length() > 500) {
            combined = combined.substring(combined.length() - 500);
        }
        order.setNote(combined);
    }

    @Transactional
    public Order startShippingByAdmin(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán của đơn hàng"));

        String currentOrderStatus = normalizeOrderStatus(order.getStatus());
        String currentPaymentStatus = normalizePaymentStatus(payment.getStatus());

        if ("CANCELLED".equals(currentOrderStatus) || "CANCELLED".equals(currentPaymentStatus)) {
            throw new RuntimeException("Không thể bắt đầu giao hàng cho đơn đã hủy");
        }

        if ("PAID".equals(currentOrderStatus)) {
            throw new RuntimeException("Đơn hàng đã hoàn tất");
        }

        if ("SHIPPING".equals(currentOrderStatus)) {
            return order;
        }

        if (!"PENDING_PAYMENT".equals(currentOrderStatus)) {
            throw new RuntimeException("Trạng thái đơn hàng không hợp lệ để bắt đầu giao");
        }

        order.setStatus("SHIPPING");
        return orderRepository.save(order);
    }

    @Transactional
    public Order completeDeliveryByAdmin(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán của đơn hàng"));

        String currentOrderStatus = normalizeOrderStatus(order.getStatus());
        String currentPaymentStatus = normalizePaymentStatus(payment.getStatus());

        if ("CANCELLED".equals(currentOrderStatus) || "CANCELLED".equals(currentPaymentStatus)) {
            throw new RuntimeException("Không thể hoàn tất giao hàng cho đơn đã hủy");
        }

        if ("PAID".equals(currentOrderStatus)) {
            throw new RuntimeException("Đơn hàng đã hoàn tất trước đó");
        }

        if (!"SHIPPING".equals(currentOrderStatus)) {
            throw new RuntimeException("Chỉ có thể hoàn tất khi đơn đang vận chuyển");
        }

        order.setStatus("PAID");
        if (!"PAID".equals(currentPaymentStatus)) {
            payment.setStatus("PAID");
            paymentRepository.save(payment);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public String getLatestPaymentStatusForOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        return paymentRepository.findTopByOrderIDOrderByIdDesc(order)
                .map(Payment::getStatus)
                .map(this::normalizePaymentStatus)
                .orElse("UNKNOWN");
    }

    private GetPaidOrderWithDetailsDto mapOrderToDetailsDto(Order order) {
        var paymentOpt = paymentRepository.findTopByOrderIDOrderByIdDesc(order);
        String paymentStatus = paymentOpt
                .map(Payment::getStatus)
                .map(this::normalizePaymentStatus)
                .orElse("UNKNOWN");
        String paymentMethod = paymentOpt
                .map(Payment::getMethod)
                .map(this::normalizePaymentMethod)
                .orElse("UNKNOWN");

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID_Id(order.getId());
        GetPaidOrderWithDetailsDto dto = new GetPaidOrderWithDetailsDto(order, paymentStatus, paymentMethod, orderDetails);
        dto.setOrderStatus(normalizeOrderStatus(order.getStatus()));
        return dto;
    }

    private String formatAddressSnapshot(Address address) {
        if (address == null) {
            return null;
        }

        return Stream.of(
                        address.getUnitNumber(),
                        address.getStreetNumber(),
                        address.getAddressLine1(),
                        address.getAddressLine2(),
                        address.getRegion(),
                        address.getCity(),
                        address.getPostalCode()
                )
                .filter(part -> part != null && !part.trim().isEmpty())
                .map(String::trim)
                .reduce((a, b) -> a + ", " + b)
                .orElse(null);
    }

    private Order cancelOrderInternal(Order order, boolean restoreToCart) {
        String currentOrderStatus = normalizeOrderStatus(order.getStatus());
        if ("CANCELLED".equals(currentOrderStatus)) {
            throw new RuntimeException("Đơn hàng đã được hủy trước đó");
        }

        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order).orElse(null);
        String paymentStatus = payment != null
                ? normalizePaymentStatus(payment.getStatus())
                : "";

        if ("PAID".equals(paymentStatus) || "PAID".equals(currentOrderStatus)) {
            throw new RuntimeException("Không thể hủy đơn hàng đã thanh toán");
        }

        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(order.getId());
        for (OrderDetail detail : details) {
            ProductColor productColor = detail.getProductColorID();
            if (productColor == null) {
                continue;
            }

            Integer currentStock = productColor.getStockQuantity() == null ? 0 : productColor.getStockQuantity();
            Integer quantity = detail.getQuantity() == null ? 0 : detail.getQuantity();
            productColor.setStockQuantity(currentStock + Math.max(quantity, 0));
            productColorRepository.save(productColor);

            if (restoreToCart && quantity > 0 && order.getAccountID() != null) {
                restoreItemToCart(order.getAccountID(), productColor, quantity);
            }
        }

        DiscountCoupon coupon = order.getCouponID();
        if (coupon != null) {
            Integer currentQuantity = coupon.getQuantity() == null ? 0 : coupon.getQuantity();
            coupon.setQuantity(currentQuantity + 1);
            discountCouponRepository.save(coupon);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        if (payment != null) {
            payment.setStatus("CANCELLED");
            paymentRepository.save(payment);
        }

        return order;
    }

    private void restoreItemToCart(Account account, ProductColor productColor, Integer quantity) {
        if (account == null || productColor == null || quantity == null || quantity <= 0) {
            return;
        }

        Cart cart = cartRepository.findByAccountID_Id(account.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setAccountID(account);
                    return cartRepository.save(newCart);
                });

        List<CartItem> existingItems = cartItemRepository.findByCartID_IdAndProductColorID_Id(cart.getId(), productColor.getId());
        CartItem cartItem;
        if (existingItems.isEmpty()) {
            cartItem = new CartItem();
            cartItem.setCartID(cart);
            cartItem.setProductColorID(productColor);
            cartItem.setQuantity(quantity);
        } else {
            cartItem = existingItems.get(0);
            Integer currentQty = cartItem.getQuantity() == null ? 0 : cartItem.getQuantity();
            cartItem.setQuantity(currentQty + quantity);
        }

        cartItemRepository.save(cartItem);
    }

    private DiscountCoupon validateCoupon(String couponCode, BigDecimal orderAmount) {
        DiscountCoupon coupon = discountCouponRepository.findByCouponCodeIgnoreCase(couponCode)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        if (Boolean.FALSE.equals(coupon.getActive())) {
            throw new RuntimeException("Mã giảm giá đã bị tắt");
        }

        LocalDate now = LocalDate.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian áp dụng");
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }

        Integer quantity = coupon.getQuantity() == null ? 0 : coupon.getQuantity();
        if (quantity <= 0) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }

        BigDecimal minOrder = coupon.getMinOrderValue() == null ? BigDecimal.ZERO : coupon.getMinOrderValue();
        if (orderAmount.compareTo(minOrder) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để dùng mã giảm giá");
        }

        return coupon;
    }

    private BigDecimal calculateDiscountAmount(DiscountCoupon coupon, BigDecimal orderAmount) {
        if (coupon.getDiscountValue() == null || coupon.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Giá trị giảm giá không hợp lệ");
        }

        String type = coupon.getDiscountType() == null ? "" : coupon.getDiscountType().trim().toLowerCase();
        BigDecimal discount;

        if ("percent".equals(type)) {
            discount = orderAmount
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaxDiscountValue() != null && coupon.getMaxDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
                discount = discount.min(coupon.getMaxDiscountValue());
            }
        } else if ("fixed".equals(type)) {
            discount = coupon.getDiscountValue();
        } else {
            throw new RuntimeException("Loại mã giảm giá không hợp lệ");
        }

        return discount.max(BigDecimal.ZERO).min(orderAmount);
    }

    private String normalizePaymentStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "SUCCESS" -> "PAID";
            case "UNPAID", "PAID", "CANCELLED", "UNKNOWN" -> normalized;
            case "CANCELED" -> "CANCELLED";
            case "PENDING", "PENDING_PAYMENT" -> "UNPAID";
            default -> normalized.isEmpty() ? "UNKNOWN" : normalized;
        };
    }

    private String normalizeOrderStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "COMPLETED" -> "PAID";
            case "CANCELED" -> "CANCELLED";
            case "PENDING" -> "PENDING_PAYMENT";
            case "DELIVERING", "IN_TRANSIT" -> "SHIPPING";
            case "PENDING_PAYMENT", "SHIPPING", "PAID", "CANCELLED", "PARTIAL_RETURNED", "RETURNED" -> normalized;
            default -> normalized;
        };
    }

    private String normalizePaymentMethod(String method) {
        String normalized = method == null ? "" : method.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "", "COD", "CASH" -> "COD";
            case "BANKING", "BANK", "TRANSFER", "BANK_TRANSFER" -> "BANK_TRANSFER";
            case "EWALLET", "E-WALLET", "E_WALLET" -> "E_WALLET";
            case "MB", "MBBANK", "MB BANK", "MB-BANK", "MB_BANK" -> "BANK_TRANSFER";
            default -> normalized;
        };
    }

    private String buildVietQrUrl(BigDecimal amount, String transferContent, String accountName, String accountNumber) {
        String baseUrl = safeTrim(mbBankVietQrBaseUrl);
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        long amountValue = amount.max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP).longValue();
        String bankCode = resolveVietQrBankCode();
        String template = safeTrim(mbBankVietQrTemplate);
        if (template.isEmpty()) {
            template = "compact2";
        }

        return String.format(
                "%s/%s-%s-%s.png?amount=%d&addInfo=%s&accountName=%s",
                baseUrl,
                bankCode,
                accountNumber,
                template,
                amountValue,
                urlEncode(transferContent),
                urlEncode(accountName)
        );
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String resolveVietQrBankCode() {
        String explicitBin = safeTrim(mbBankVietQrBin);
        if (!explicitBin.isEmpty()) {
            return explicitBin;
        }

        String bankCode = safeTrim(mbBankCode).toUpperCase(Locale.ROOT);
        if ("MB".equals(bankCode) || "MBBANK".equals(bankCode) || "MB BANK".equals(bankCode)) {
            return "970422";
        }
        return bankCode;
    }

    private int normalizePositive(Integer value, Integer fallback, int defaultValue) {
        if (value != null && value > 0) {
            return value;
        }
        if (fallback != null && fallback > 0) {
            return fallback;
        }
        return defaultValue;
    }

    private String trimTrailingSlash(String input) {
        if (input.endsWith("/")) {
            return input.substring(0, input.length() - 1);
        }
        return input;
    }

    private JsonNode callGhnMasterData(String path, ObjectNode bodyNode) {
        String token = safeTrim(ghnToken);
        if (token.isEmpty()) {
            throw new RuntimeException("GHN chưa được cấu hình token");
        }

        String endpoint = trimTrailingSlash(safeTrim(ghnApiBaseUrl)) + path;

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Token", token);

            if (bodyNode == null) {
                builder.GET();
            } else {
                builder.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(bodyNode)));
            }

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(builder.build(), HttpResponse.BodyHandlers.ofString());

            JsonNode rootNode = objectMapper.readTree(response.body());
            int code = rootNode.path("code").asInt(-1);
            if (response.statusCode() >= 400 || code != 200) {
                String message = rootNode.path("message").asText("GHN không trả về dữ liệu địa giới");
                throw new RuntimeException(message);
            }

            return rootNode;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Yêu cầu GHN bị gián đoạn", ex);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tải dữ liệu địa giới GHN", ex);
        }
    }

    // =========================
    // HOÀN HÀNG - ĐƠN ĐANG VẬN CHUYỂN
    // =========================
    @Transactional
    public GetPaidOrderWithDetailsDto returnShippingOrderItemByAdmin(
            Integer orderId,
            PostShippingReturnDto request
    ) {
        Order order = findOrderOrThrow(orderId);
        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order).orElse(null);

        String orderStatus = normalizeOrderStatus(order.getStatus());
        String paymentStatus = payment != null ? normalizePaymentStatus(payment.getStatus()) : "UNKNOWN";

        if ("CANCELLED".equals(orderStatus) || "CANCELLED".equals(paymentStatus)) {
            throw new RuntimeException("Không thể hoàn hàng cho đơn đã hủy");
        }

        if (!"SHIPPING".equals(orderStatus)) {
            throw new RuntimeException("Chỉ đơn đang vận chuyển mới được hoàn hàng");
        }

        BigDecimal returnAmount = processReturnSingleItem(
                order,
                request.getOrderDetailId(),
                request.getQuantity(),
                request.getNote(),
                "SHIPPING_RETURN"
        );

        updateOrderAndPaymentAfterReturn(order, payment, returnAmount);

        if (isAllOrderItemsReturned(order.getId())) {
            order.setStatus("RETURNED");
        } else {
            // Hoàn hàng một phần khi đang vận chuyển thì vẫn tiếp tục giao
            order.setStatus("SHIPPING");
        }

        updateOrderAndPaymentAfterReturn(order, payment, returnAmount);

        if (isAllOrderItemsReturned(order.getId())) {
            order.setStatus("RETURNED");
        } else {
            order.setStatus("SHIPPING");
        }

        orderRepository.save(order);

        return mapOrderToDetailsDto(findOrderOrThrow(orderId));
    }

    // =========================
    // TRẢ HÀNG - ĐƠN ĐÃ HOÀN THÀNH
    // =========================
    public GetPaidOrderWithDetailsDto findCompletedReturnableOrderByCode(String code) {
        String normalizedCode = code == null ? "" : code.trim();

        if (normalizedCode.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mã hóa đơn");
        }

        Order order = findOrderByInvoiceCode(normalizedCode);
        String orderStatus = normalizeOrderStatus(order.getStatus());

        if ("RETURNED".equals(orderStatus)) {
            throw new RuntimeException("Đơn hàng này đã trả toàn bộ");
        }

        if (!List.of("PAID", "PARTIAL_RETURNED").contains(orderStatus)) {
            throw new RuntimeException("Chỉ đơn hàng đã hoàn thành mới được trả hàng");
        }

        return mapOrderToDetailsDto(order);
    }

    private Order findOrderByInvoiceCode(String code) {
        String normalizedCode = code == null ? "" : code.trim();

        if (normalizedCode.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mã hóa đơn");
        }

        // 1. Tìm theo mã vận đơn/trackingCode, ví dụ DTVD20260429ABC
        var orderByTrackingCode = orderRepository.findByTrackingCodeIgnoreCase(normalizedCode);
        if (orderByTrackingCode.isPresent()) {
            return orderByTrackingCode.get();
        }

        // 2. Tìm theo dạng HD13, #13, 13
        String orderIdText = normalizedCode
                .replace("#", "")
                .replace("HD", "")
                .replace("hd", "")
                .trim();

        if (!orderIdText.matches("\\d+")) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        Integer orderId;
        try {
            orderId = Integer.parseInt(orderIdText);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Mã hóa đơn không hợp lệ");
        }

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    @Transactional
    public GetPaidOrderWithDetailsDto returnCompletedOrderByAdmin(
            Integer orderId,
            PostCompletedReturnRequest request
    ) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn sản phẩm cần trả");
        }

        Order order = findOrderOrThrow(orderId);
        Payment payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order).orElse(null);

        String orderStatus = normalizeOrderStatus(order.getStatus());
        String paymentStatus = payment != null ? normalizePaymentStatus(payment.getStatus()) : "UNKNOWN";

        if ("CANCELLED".equals(orderStatus) || "CANCELLED".equals(paymentStatus)) {
            throw new RuntimeException("Không thể trả hàng cho đơn đã hủy");
        }

        if ("RETURNED".equals(orderStatus)) {
            throw new RuntimeException("Đơn hàng này đã trả toàn bộ");
        }

        if (!List.of("PAID", "PARTIAL_RETURNED").contains(orderStatus)) {
            throw new RuntimeException("Chỉ đơn hàng đã hoàn thành mới được trả hàng");
        }

        BigDecimal totalReturnAmount = BigDecimal.ZERO;

        for (PostCompletedReturnRequest.Item item : request.getItems()) {
            totalReturnAmount = totalReturnAmount.add(
                    processReturnSingleItem(
                            order,
                            item.getOrderDetailId(),
                            item.getQuantity(),
                            item.getNote() != null && !item.getNote().trim().isEmpty()
                                    ? item.getNote()
                                    : request.getNote(),
                            "COMPLETED_RETURN"
                    )
            );
        }

        updateOrderAndPaymentAfterReturn(order, payment, totalReturnAmount);

        if (isAllOrderItemsReturned(order.getId())) {
            order.setStatus("RETURNED");
        } else {
            order.setStatus("PARTIAL_RETURNED");
        }

        orderRepository.save(order);
        return mapOrderToDetailsDto(findOrderOrThrow(orderId));
    }

    private Order findOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    private BigDecimal processReturnSingleItem(
            Order order,
            Integer orderDetailId,
            Integer quantity,
            String note,
            String type
    ) {
        if (orderDetailId == null) {
            throw new RuntimeException("Thiếu sản phẩm cần xử lý");
        }

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        String normalizedNote = note == null ? "" : note.trim();
        if (normalizedNote.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập ghi chú");
        }

        OrderDetail detail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong đơn"));

        if (detail.getOrderID() == null || !detail.getOrderID().getId().equals(order.getId())) {
            throw new RuntimeException("Sản phẩm không thuộc đơn hàng này");
        }

        ProductColor productColor = detail.getProductColorID();
        if (productColor == null) {
            throw new RuntimeException("Biến thể sản phẩm không hợp lệ");
        }

        int boughtQuantity = detail.getQuantity() == null ? 0 : detail.getQuantity();
        int returnedQuantity = detail.getReturnedQuantity() == null ? 0 : detail.getReturnedQuantity();
        int returnableQuantity = boughtQuantity - returnedQuantity;

        if (returnableQuantity <= 0) {
            throw new RuntimeException("Sản phẩm này đã xử lý hết số lượng");
        }

        if (quantity > returnableQuantity) {
            throw new RuntimeException("Số lượng vượt quá số lượng còn lại");
        }

        productColor.setStockQuantity((productColor.getStockQuantity() == null ? 0 : productColor.getStockQuantity()) + quantity);
        productColorRepository.save(productColor);

        detail.setReturnedQuantity(returnedQuantity + quantity);

        if ("SHIPPING_RETURN".equals(type)) {
            int currentShippingReturned = detail.getShippingReturnedQuantity() == null
                    ? 0
                    : detail.getShippingReturnedQuantity();

            detail.setShippingReturnedQuantity(currentShippingReturned + quantity);
        } else if ("COMPLETED_RETURN".equals(type)) {
            int currentCompletedReturned = detail.getCompletedReturnedQuantity() == null
                    ? 0
                    : detail.getCompletedReturnedQuantity();

            detail.setCompletedReturnedQuantity(currentCompletedReturned + quantity);
        }
        orderDetailRepository.save(detail);

        BigDecimal price = detail.getPrice() == null ? BigDecimal.ZERO : detail.getPrice();
        BigDecimal returnAmount = price.multiply(BigDecimal.valueOf(quantity));

        appendReturnNote(order, detail, quantity, returnAmount, normalizedNote, type);

        return returnAmount;
    }

    private void updateOrderAndPaymentAfterReturn(Order order, Payment payment, BigDecimal returnAmount) {
        BigDecimal safeReturnAmount = returnAmount == null ? BigDecimal.ZERO : returnAmount.max(BigDecimal.ZERO);

        BigDecimal currentTotal = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        order.setTotalAmount(currentTotal.subtract(safeReturnAmount).max(BigDecimal.ZERO));

        if (payment != null && payment.getAmount() != null) {
            payment.setAmount(payment.getAmount().subtract(safeReturnAmount).max(BigDecimal.ZERO));
            paymentRepository.save(payment);
        }
    }

    private boolean isAllOrderItemsReturned(Integer orderId) {
        List<OrderDetail> details = orderDetailRepository.findByOrderID_Id(orderId);

        if (details == null || details.isEmpty()) {
            return false;
        }

        return details.stream().allMatch(detail -> {
            int quantity = detail.getQuantity() == null ? 0 : detail.getQuantity();
            int returnedQuantity = detail.getReturnedQuantity() == null ? 0 : detail.getReturnedQuantity();
            return quantity > 0 && returnedQuantity >= quantity;
        });
    }

    private void appendReturnNote(
            Order order,
            OrderDetail detail,
            Integer quantity,
            BigDecimal amount,
            String note,
            String type
    ) {
        String productName = "Sản phẩm";

        if (detail.getProductColorID() != null
                && detail.getProductColorID().getProductID() != null
                && detail.getProductColorID().getProductID().getProductName() != null) {
            productName = detail.getProductColorID().getProductID().getProductName();
        }

        String label = "SHIPPING_RETURN".equals(type) ? "HOAN_HANG" : "TRA_HANG";
        String currentNote = order.getNote() == null ? "" : order.getNote().trim();
        String timeText = LocalDateTime.now().format(REVERT_REASON_TIME_FORMAT);

        String line = "[" + label + " " + timeText + "] "
                + productName
                + " - SL: " + quantity
                + " - Tiền: " + amount
                + " - Ghi chú: " + note;

        if (currentNote.isEmpty()) {
            order.setNote(line);
            return;
        }

        String combined = currentNote + "\n" + line;
        if (combined.length() > 500) {
            combined = combined.substring(combined.length() - 500);
        }

        order.setNote(combined);
    }
}