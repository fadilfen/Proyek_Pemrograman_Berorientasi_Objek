# Cara Run di VSCode

## Persiapan

### 1. Install Extension
Pastikan extension berikut sudah terinstall di VSCode:
- **Extension Pack for Java** (Microsoft)
  - Language Support for Java
  - Debugger for Java
  - Test Runner for Java
  - Maven for Java
  - Project Manager for Java

### 2. Reload VSCode
Setelah install extension, reload VSCode:
- Tekan `Ctrl+Shift+P`
- Ketik "Reload Window"
- Enter

## Cara Run Aplikasi

### Metode 1: Tombol Run/Debug (RECOMMENDED)
1. Buka file `gui/src/tubes_a11/Main.java`
2. Klik tombol **Run** (▶️) atau **Debug** di atas method `main`
3. Atau tekan `F5` untuk debug
4. Atau tekan `Ctrl+F5` untuk run tanpa debug

### Metode 2: Run Configuration
1. Tekan `F5` atau klik menu **Run > Start Debugging**
2. Pilih "Run MindFull App" dari dropdown
3. Aplikasi akan jalan

### Metode 3: Klik Kanan
1. Buka file `gui/src/tubes_a11/Main.java`
2. Klik kanan di editor
3. Pilih **Run Java**

### Metode 4: Command Palette
1. Tekan `Ctrl+Shift+P`
2. Ketik "Java: Run"
3. Pilih `tubes_a11.Main`

## Troubleshooting

### Error: "Cannot find main class"
**Solusi:**
1. Clean Java workspace:
   - `Ctrl+Shift+P`
   - Ketik "Java: Clean Java Language Server Workspace"
   - Restart VSCode

2. Rebuild project:
   - `Ctrl+Shift+P`
   - Ketik "Java: Force Java Compilation"
   - Full

### Error: "ClassNotFoundException: mysql.cj.jdbc.Driver"
**Solusi:**
1. Pastikan file `gui/lib/mysql-connector.jar` ada
2. Refresh Java project:
   - `Ctrl+Shift+P`
   - Ketik "Java: Clean Java Language Server Workspace"
   - Restart VSCode

### Error: "Build failed"
**Solusi:**
1. Compile manual dulu:
   ```bash
   cd gui
   compile.bat
   ```
2. Refresh VSCode (F5)

### Aplikasi tidak muncul / stuck
**Solusi:**
1. Pastikan Laragon MySQL sudah running
2. Pastikan database `mindfull_db` sudah di-import
3. Check terminal output untuk error message

## Tips

### Auto Compile on Save
Tambahkan di `settings.json`:
```json
{
    "java.autobuild.enabled": true
}
```

### Faster Compilation
Jika compile lambat, disable auto-build:
```json
{
    "java.autobuild.enabled": false
}
```
Lalu compile manual dengan `Ctrl+Shift+B`

### Lihat Output
Panel Output akan menampilkan:
- Compilation errors
- Runtime errors
- Database connection status
- [DB] logs

### Debug Mode
Untuk debug:
1. Set breakpoint di code (klik kiri di line number)
2. Tekan `F5` untuk start debugging
3. Gunakan debug controls (Step Over, Step Into, Continue)

## Shortcut Penting

| Shortcut | Fungsi |
|----------|--------|
| `F5` | Start Debugging |
| `Ctrl+F5` | Run without Debug |
| `Ctrl+Shift+B` | Build/Compile |
| `Ctrl+Shift+P` | Command Palette |
| `Ctrl+K Ctrl+S` | Keyboard Shortcuts |

## File Konfigurasi

Sudah dibuat otomatis:
- `.vscode/launch.json` - Run configuration
- `.vscode/settings.json` - Java project settings
- `.vscode/tasks.json` - Build tasks
- `.classpath` - Eclipse/VSCode classpath
- `.project` - Project definition

Jangan hapus file-file ini!
