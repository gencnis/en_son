# En Son – Google Play release kit

Everything needed to publish En Son, in the order you'll need it.

## 1. Create the upload key (once, on your machine)

Play signs the app for users ("Play App Signing"); you sign uploads with
your own **upload key**. Create it outside the repo and back it up together
with its password (e.g. in a password manager). If it's lost, Play support
can reset it, but that takes days.

```bash
keytool -genkeypair -v -keystore ~/Desktop/en-son-upload.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
```

Then copy `keystore.properties.example` to `keystore.properties` in the
project root (it is git-ignored) and fill it in:

```properties
storeFile=../en-son-upload.jks
storePassword=…
keyAlias=upload
keyPassword=…
```

## 2. Build the bundle

```bash
./gradlew :app:bundleRelease
```

Upload `app/build/outputs/bundle/release/app-release.aab`. Without
`keystore.properties` the bundle is unsigned and Play will reject it.

For every later upload, raise `versionCode` (and usually `versionName`) in
`app/build.gradle.kts`.

## 3. Store listing (Ana mağaza girişi)

| Field | Value |
| --- | --- |
| App name | En Son |
| Default language | Türkçe – tr-TR |
| Category | Verimlilik (Productivity) |
| Contact email | nisagenc.dev@gmail.com |
| Privacy policy | https://gencnis.github.io/en_son/privacy.html |

**Short description** (77/80):

> Bir şeyi en son ne zaman yaptığını kaydet, kaç gün geçtiğini bir bakışta gör.

**Full description:**

> Çiçekleri en son ne zaman suladın? Çarşafları ne zaman değiştirdin? Kahve makinesini en son ne zaman temizledin?
>
> En Son, küçük ama unutması kolay şeyleri senin yerine hatırlar. Bir şeyi yaptığında tek dokunuşla kaydet; üzerinden kaç gün geçtiğini büyük ve net biçimde gör.
>
> • Tek dokunuşla “Bugün yaptım”, yanlışlıkla bastıysan “Geri al”
> • İstersen tekrar aralığı belirle: “3 gün kaldı”, “Bugün zamanı”, “2 gün gecikti”
> • Her takibin geçmişi, tarihleri ve kısa notlarıyla
> • Kişisel, Ev ve Diğer kategorileri; önce yaklaşanlara göre sıralama
> • Arşiv: artık takip etmediklerini silmeden kaldır
> • 22 ikon ve 6 yumuşak renkle kendi kartlarını oluştur
> • Açık ve koyu tema
> • Regl başlangıcı takibi yalnızca tarih geçmişini tutar; tahmin veya yorum yapmaz
>
> Gizliliğin önce gelir:
> • Hesap, internet bağlantısı, reklam veya analitik yok
> • Tüm verilerin yalnızca telefonunda saklanır
> • Telefon değiştirirken kendi yedek dosyanı oluşturup geri yükleyebilirsin
>
> Sade, sakin ve tamamen ücretsiz.

**Graphics** (in this folder):

| Asset | File |
| --- | --- |
| App icon 512×512 | `graphics/icon-512.png` |
| Feature graphic 1024×500 | `graphics/feature-graphic-1024x500.png` |
| Phone screenshots 1080×1920 (9:16) | `screenshots/1-ana-ekran.png` … `5-ayarlar.png` |

Screenshots were taken on an emulator from a demo backup; no demo data
ships in the app. The settings screenshot shows the About note, so retake it
if that text changes.

To regenerate the icon and feature graphic after a logo change (emulator
running), then copy them from the device:

```bash
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.gencnis.enson.tools.StoreGraphicsGenerator -Pandroid.testInstrumentationRunnerArguments.storeGraphics=true -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true
```

```bash
adb pull /sdcard/Android/data/com.gencnis.enson/files/store/ app_store/graphics/
```

## 4. App content (Uygulama içeriği) answers

| Section | Answer |
| --- | --- |
| Privacy policy | URL above (enable GitHub Pages: Settings → Pages → `main`, `/docs`) |
| Ads | No, the app has no ads |
| App access | All functionality is available without special access (no login) |
| Content rating (IARC) | Category: Utility / Productivity. Answer **No** to violence, sexuality, language, controlled substances, gambling, user interaction, sharing location, purchases. Expected rating: 3+ / Everyone |
| Target audience | 13 and over (13–15, 16–17, 18+). Don't select under-13 groups, or Families policy rules apply |
| Data safety | **Does your app collect or share user data? → No.** Data is processed only on the device and never sent off it, which Play does not count as "collected". Backup files are created and stored by the user. |
| Health apps | Declare the health feature: the app includes **period tracking** (dates only, no predictions, not a medical device) |
| Account deletion | Not applicable: the app has no accounts |
| Government / financial / news / COVID | No |

## 5. Before production

* **Seller status**: declare it in the Play Console before February 2027.
  As an individual with a free app and no in-app purchases you are most
  likely a *non-trader*.
* **Closed testing**: new personal developer accounts must run a closed
  test with at least 12 testers for 14 days before they can apply for
  production access. Create a closed testing track, upload the same bundle
  and invite testers by email list.
