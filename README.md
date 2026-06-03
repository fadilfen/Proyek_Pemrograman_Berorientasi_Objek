# OOP_A11 - Tugas Besar

Repository untuk Tugas Besar Pemrograman Berorientasi Objek - Kelompok A11

## Struktur Project

```
oop_a11/
├── demo/          # Spring Boot Web Application (Backend)
│   ├── src/
│   ├── target/
│   └── pom.xml
│
└── gui/           # Java Swing Desktop Application (GUI)
    ├── src/
    │   ├── database/      # Database connection & helpers
    │   └── tubes_a11/     # Main application code
    ├── lib/               # External libraries (MySQL connector)
    ├── docs/              # Documentation & SQL files
    ├── README.md          # GUI application documentation
    └── .gitignore
```

## 2 Aplikasi dalam 1 Project

### 1. Demo (Spring Boot)
- **Path:** `demo/`
- **Type:** Web Application
- **Framework:** Spring Boot 3.x
- **Port:** 8080 (default)
- **Database:** MySQL (mindfull_db)
- **Run:** `mvn spring-boot:run` atau run DemoApplication.java

### 2. GUI (Java Swing)
- **Path:** `gui/`
- **Type:** Desktop Application
- **Framework:** Java Swing
- **Database:** MySQL (mindfull_db) - shared dengan demo
- **Run:** `java tubes_a11.Main`
- **Features:** Parent-Child Account System, Screen Time Tracker

## Shared Database

Kedua aplikasi menggunakan database yang sama:
- **Database:** `mindfull_db`
- **Server:** MySQL (Laragon)
- **SQL File:** `gui/docs/database.sql`

## Quick Start

### Setup Database
1. Start Laragon/MySQL
2. Import SQL: `gui/docs/database.sql`

### Run Demo (Web)
```bash
cd demo
mvn spring-boot:run
```
Akses: http://localhost:8080

### Run GUI (Desktop)
```bash
cd gui/src
javac -cp ".;../lib/mysql-connector.jar" tubes_a11/*.java database/*.java
java -cp ".;../lib/mysql-connector.jar" tubes_a11.Main
```

## Documentation

- GUI Application: `gui/README.md`
- Demo Application: `demo/README.md` (jika ada)

## Team

Kelompok A11 - Politeknik Negeri Bandung
