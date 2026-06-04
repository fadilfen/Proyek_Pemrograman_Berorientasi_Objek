package com.screentimetracker.demo.controller;

import com.screentimetracker.demo.model.Notification;
import com.screentimetracker.demo.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NotificationController menyediakan API REST untuk mengelola notifikasi user.
 * Digunakan oleh Javascript di navbar untuk menampilkan dropdown notifikasi secara dinamis.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notifService;

    public NotificationController(NotificationService notifService) {
        this.notifService = notifService;
    }

    private Long getUserId(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    /**
     * Mendapatkan daftar 10 notifikasi terbaru dan jumlah yang belum dibaca.
     */
    @GetMapping
    public ResponseEntity<?> getNotifications(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User belum login");
        }

        List<Notification> list = notifService.getNotifikasiTerbaru(userId);
        long unreadCount = notifService.countUnread(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", list);
        response.put("unreadCount", unreadCount);

        return ResponseEntity.ok(response);
    }

    /**
     * Menandai satu notifikasi sebagai sudah dibaca.
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User belum login");
        }

        boolean success = notifService.tandaiBaca(id, userId);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Gagal menandai dibaca");
    }

    /**
     * Menandai semua notifikasi sebagai sudah dibaca.
     */
    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User belum login");
        }

        notifService.tandaiSemuaBaca(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Menghapus satu notifikasi.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User belum login");
        }

        boolean success = notifService.hapusNotifikasi(id, userId);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Gagal menghapus notifikasi");
    }
}
