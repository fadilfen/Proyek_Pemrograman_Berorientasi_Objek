package com.screentimetracker.demo.service;

import org.springframework.stereotype.Service;

/**
 * PaymentService menangani logika pembayaran.
 * Untuk proyek ini, QRIS menggunakan QR code warung statis (tidak memerlukan Midtrans).
 * Kelas ini dipertahankan untuk kemungkinan integrasi di masa mendatang.
 */
@Service
public class PaymentService {

    /**
     * Mendapatkan URL QR code statis untuk tampilan QRIS warung.
     * @return Path ke gambar QR code statis
     */
    public String getQrisImagePath() {
        return "qris-warung.png";
    }
}