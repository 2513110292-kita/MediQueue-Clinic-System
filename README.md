# MediQueue-Clinic-System
Project ระบบจัดการคิวคลินิก (Java OOP + Swing)

## Features
- เพิ่ม / ลบ / แก้ไขนัดคิว
- ระบบคิว (Queue Number)
- กันเวลาซ้ำ
- ค้นหา
- ซ่อนเบอร์ + Master Unlock
- ระบบแสดงรายการนัดหมายทั้งหมด
- ผู้ใช้สามารถแก้ไขหรือยกเลิกการนัดหมายได้
  
## โปรเจกต์นี้ใช้แนวคิด OOP หลายด้าน ได้แก่
- Class และ Object ในการสร้างโครงสร้างข้อมูล
- Encapsulation เพื่อปกป้องข้อมูลผ่าน getter
- Abstraction โดยซ่อน logic ไว้ใน AppointmentManager
- Inheritance ผ่านการสืบทอด JFrame
- Polymorphism ผ่าน ActionListener
- Composition โดยการใช้ object ร่วมกันระหว่างคลาส
- Separation of Concerns โดยแยก UI และ Model ออกจากกัน

## Layout
src/
 ├── model/
 ├── ui/
 └── images/
README.md
