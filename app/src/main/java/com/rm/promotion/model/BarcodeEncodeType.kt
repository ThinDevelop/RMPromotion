package com.rm.promotion.model

enum class BarcodeEncodeType(val id: Int) {
    UPC_A(0),
    UPC_E(1),
    EAN13(2),
    EAN8(3),
    CODE39(4),
    ITF(5),
    CODABAR(6),
    CODE93(7),
    CODE128A(8),
    CODE128B(9),
    CODE128C(10)
}