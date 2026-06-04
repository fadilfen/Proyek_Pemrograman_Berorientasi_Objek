package com.screentimetracker.demo.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.screentimetracker.demo.model.AktivitasDigital;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * PdfService bertanggung jawab untuk menghasilkan dokumen PDF laporan kesehatan
 * mental pengguna menggunakan pustaka iText7.
 */
@Service
public class PdfService {

    /**
     * Membuat dokumen PDF dari data laporan pengguna.
     *
     * @param data Objek LaporanData berisi informasi screen time dan skor kesehatan.
     * @return array byte yang berisi dokumen PDF.
     */
    public byte[] generatePdfReport(MindFullService.LaporanData data) {
        if (data == null) {
            return new byte[0];
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Judul Dokumen
            Paragraph title = new Paragraph("LAPORAN DIGITAL WELLNESS - MINDFULL")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Pantau Screen Time untuk Kesehatan Mental yang Lebih Baik")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subtitle);
            document.add(new Paragraph("\n"));

            // Informasi Pengguna
            document.add(new Paragraph("Informasi Pengguna:").setBold().setFontSize(12));
            document.add(new Paragraph("Nama Lengkap : " + data.user.getNamaUser()));
            document.add(new Paragraph("Username     : " + data.user.getUsername()));
            document.add(new Paragraph("Tanggal      : " + data.tanggal.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))));
            document.add(new Paragraph("\n"));

            // Ringkasan Kesehatan
            document.add(new Paragraph("Ringkasan Kesehatan:").setBold().setFontSize(12));
            document.add(new Paragraph("Skor Kesehatan Mental: " + data.skor + "/100 (" + data.kategori + ")"));
            document.add(new Paragraph("Total Screen Time    : " + data.totalDurasi + " menit (" + (data.totalDurasi / 60) + " jam " + (data.totalDurasi % 60) + " menit)"));
            document.add(new Paragraph("\n"));

            // Tabel Aktivitas
            document.add(new Paragraph("Detail Aktivitas Layar:").setBold().setFontSize(12));
            float[] columnWidths = {150F, 100F, 100F, 100F};
            Table table = new Table(columnWidths);
            
            // Header
            table.addHeaderCell(new Cell().add(new Paragraph("Nama Aplikasi").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Durasi (Menit)").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Batas (Menit)").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()));

            // Baris Data
            for (AktivitasDigital act : data.aktivitasList) {
                table.addCell(new Cell().add(new Paragraph(act.getNamaAplikasi())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(act.getDurasiMenit()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(act.getBatasDurasi()))));
                table.addCell(new Cell().add(new Paragraph(act.melebihiBatas() ? "Over Limit ⚠️" : "Aman")));
            }
            document.add(table);
            document.add(new Paragraph("\n"));

            // Rekomendasi
            document.add(new Paragraph("Rekomendasi Wellness:").setBold().setFontSize(12));
            document.add(new Paragraph(data.rekomendasi));

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
