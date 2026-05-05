package com.screentimetracker.demo.service;

import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * PaymentService menangani integrasi dengan Midtrans untuk pembayaran QRIS.
 */
@Service
public class PaymentService {

    private final MidtransSnapApi snapApi;

    public PaymentService(@Value("${midtrans.server-key}") String serverKey,
                          @Value("${midtrans.client-key}") String clientKey,
                          @Value("${midtrans.is-production}") boolean isProduction) {
        Config config = Config.builder()
                .setServerKey(serverKey)
                .setClientKey(clientKey)
                .setIsProduction(isProduction)
                .build();
        this.snapApi = new ConfigFactory(config).getSnapApi();
    }

    /**
     * Membuat transaksi QRIS untuk top up token.
     * @param userId ID pengguna
     * @param jumlahToken Jumlah token yang dibeli
     * @param hargaPerToken Harga per token dalam Rupiah
     * @return Snap token untuk generate QR code
     */
    public String createQrisTransaction(Long userId, int jumlahToken, int hargaPerToken) throws MidtransError {
        String orderId = "TOPUP-" + userId + "-" + UUID.randomUUID().toString().substring(0, 8);

        // Buat parameter transaksi sebagai Map
        Map<String, Object> params = new HashMap<>();
        
        // Transaction details
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", (long) (jumlahToken * hargaPerToken));
        params.put("transaction_details", transactionDetails);
        
        // Item details
        List<Map<String, Object>> itemDetailsList = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", "TOKEN-" + jumlahToken);
        item.put("price", (long) hargaPerToken);
        item.put("quantity", jumlahToken);
        item.put("name", jumlahToken + " Token MindFull");
        itemDetailsList.add(item);
        params.put("item_details", itemDetailsList);
        
        // Customer details
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("id", userId.toString());
        params.put("customer_details", customerDetails);
        
        // QRIS specific
        Map<String, Object> qris = new HashMap<>();
        qris.put("acquirer", "gopay");
        params.put("qris", qris);

        // Buat Snap token menggunakan instance SnapApi
        JSONObject response = snapApi.createTransaction(params);
        return response.getString("token");
    }

    /**
     * Mendapatkan URL QR code dari snap token.
     * @param snapToken Token dari createQrisTransaction
     * @return URL untuk menampilkan QR code
     */
    public String getQrisUrl(String snapToken) {
        return "https://app.sandbox.midtrans.com/snap/v2/vtweb/" + snapToken;
    }
}