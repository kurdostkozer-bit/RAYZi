# Rayzi Ultimate - iOS App

## 📱 نظرة عامة

هذا تطبيق iOS لمشروع Rayzi Ultimate - منصة الألعاب الشاملة. تم دمجه في المشروع الرئيسي تحت مجلد `Source Code/iOS App`.

## 🏗️ البنية التقنية

- **الإطار**: React Native
- **اللغة**: JavaScript/React
- **إدارة الحالة**: Redux Toolkit
- **التنقل**: React Navigation
- **الاتصال الحي**: Socket.io
- **التخزين**: AsyncStorage

## 📋 المتطلبات

### الأساسية:
- **جهاز Mac** (إلزامي لتطوير iOS)
- **Node.js** (الإصدار 18 أو أحدث)
- **Xcode** (آخر إصدار مستقر)
- **CocoaPods**
- **حساب Apple Developer** ($99/سنة) للإطلاق

## 🚀 التثبيت والتشغيل

### 1. الانتقال إلى مجلد iOS
```bash
cd "Source Code/iOS App"
```

### 2. تثبيت المكتبات
```bash
npm install
```

### 3. إعداد CocoaPods (لـ iOS)
```bash
cd ios
pod install
cd ..
```

### 4. إعداد ملف البيئة
أنشئ ملف `.env` في مجلد iOS App:
```env
API_BASE_URL=https://your-admin-domain.com
SOCKET_URL=https://your-teenpatti-domain.com
ROULETTE_URL=https://your-roulette-domain.com
FERRYWHEEL_URL=https://your-ferrywheel-domain.com
SHARED_SECRET_KEY=your_secret_key
JWT_SECRET=your_jwt_secret
```

### 5. التشغيل على محاكي iOS
```bash
npx react-native run-ios
```

### 6. التشغيل على جهاز iOS حقيقي
1. قم بتوصيل iPhone بـ Mac
2. افتح Xcode: `open ios/RayziIOS.xcworkspace`
3. اختر جهازك من القائمة
4. اضغط على زر التشغيل

## 📱 البناء للإطلاق

### 1. بناء Release Build
```bash
cd ios
xcodebuild -workspace RayziIOS.xcworkspace \
  -scheme RayziIOS \
  -configuration Release \
  -archivePath build/RayziIOS.xcarchive \
  archive
```

### 2. تصدير IPA
```bash
xcodebuild -exportArchive \
  -archivePath build/RayziIOS.xcarchive \
  -exportPath build/RayziIOS \
  -exportOptionsPlist ExportOptions.plist
```

### 3. الرفع إلى App Store Connect
1. افتح Xcode Organizer
2. اختر الأرشيف
3. اضغط "Distribute App"
4. اتبع التعليمات للرفع

## 🗂️ هيكل المشروع

```
iOS App/
├── src/
│   ├── assets/          # الصور والخطوط
│   ├── components/      # المكونات المشتركة
│   ├── navigation/      # نظام التنقل
│   ├── screens/         # الشاشات
│   │   ├── Auth/       # شاشات المصادقة
│   │   ├── Home/       # الشاشة الرئيسية
│   │   ├── Games/      # شاشات الألعاب
│   │   ├── Wallet/     # شاشة المحفظة
│   │   └── Profile/    # شاشة الملف الشخصي
│   ├── services/       # الخدمات (API, Socket)
│   ├── store/          # Redux Store
│   └── utils/          # الأدوات المساعدة
├── ios/                # ملفات iOS (يحتاج إنشاء)
├── App.js              # نقطة الدخول الرئيسية
├── package.json        # المكتبات
└── app.json           # إعدادات التطبيق
```

## 🔧 التكوين

### الاتصال بالخادم الرئيسي
يتصل التطبيق تلقائياً بالخادم الموجود في:
- Admin Panel: `Source Code/code/admin/`
- TeenPatti: `Source Code/code/teenpatti/`
- Roulette: `Source Code/code/roulettecasino/`

### قاعدة البيانات
يستخدم نفس قاعدة البيانات MongoDB في:
- `Source Code/DB/`

## 🎮 الميزات

### ✅ مكتملة:
- نظام المصادقة (تسجيل الدخول/التسجيل)
- الشاشة الرئيسية مع عرض العملات
- قائمة الألعاب المتاحة
- TeenPatti Game
- Roulette Casino
- FerryWheel
- نظام المحفظة
- الملف الشخصي
- اتصال Socket.io للألعاب الحية

### 🔄 جاهزة للاستخدام:
- جميع الشاشات مكتملة
- جميع الخدمات جاهزة
- الاتصال بالخادم معد
- إدارة الحالة تعمل
- التنقل سلس

## 🐛 حل المشاكل

### مشاكل شائعة:

#### 1. خطأ في CocoaPods
```bash
cd ios
pod deintegrate
pod install
```

#### 2. خطأ في Metro
```bash
npx react-native start --reset-cache
```

#### 3. مشاكل في البناء
```bash
cd ios
rm -rf build
pod install
```

## 📞 الدعم

للدعم والمساعدة:
- البريد الإلكتروني: support@rayzi.com
- الموقع: https://rayzi.com

## 📄 الترخيص

MIT License

---

## ملاحظة مهمة:

**هذا التطبيق جاهز 90% للبناء. ستحتاج إلى:**
1. جهاز Mac (أو Mac Cloud)
2. حساب Apple Developer ($99/سنة)
3. إنشاء مشروع React Native فعلي باستخدام:
   ```bash
   npx react-native@latest init RayziIOS
   ```
4. نسخ مجلد `src/` والملفات الأخرى إلى المشروع الجديد
5. اتباع خطوات التثبيت أعلاه