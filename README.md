# Farm2Home Backend (-CrudApi)

Spring Boot backend for the Farm2Home application.

---

## Running the backend

### 1. Environment Configuration (`.env`)
- Backend root folder (`UserApp/`) mein single `.env` file hoti hai jo automatically read hoti hai.
- Yeh file `.gitignore` mein added hai, isko **kabhi commit nahi karna hai**.
- Yeh file IntelliJ IDEA aur `run-backend.ps1` dono dwara automatically read hoti hai.
- Required variable names:
  - `RAZORPAY_KEY_ID`
  - `RAZORPAY_KEY_SECRET`
  - `DB_PASSWORD`
  - `JWT_SECRET`

### 2. Running in IntelliJ IDEA (Direct Run)
- IntelliJ IDEA automatically sets the working directory to the module root (`UserApp`).
- Spring Boot `application.properties` mein `spring.config.import=optional:file:./.env[.properties]` configured hai.
- Iska matlab aapko IntelliJ Run Configuration mein koi bhi environment variables manually daalne ki zaroorat **nahi** hai.
- Bas IntelliJ mein `UserAppApplication` par jaake green **Run** button click karein.
- **Note:** Agar koi terminal ya PowerShell process pehle se port 8080 par chal raha ho, toh IntelliJ mein run karne se pehle use stop karein.

### 3. Running from Terminal / PowerShell (`run-backend.ps1`)
- Agar aap terminal se backend chalana chahte hain, toh project root mein `run-backend.ps1` script use karein:
  ```powershell
  .\run-backend.ps1
  ```
- Yeh script `.env` parse karke process environment variables set karta hai, missing variables verify karta hai, aur Maven wrapper se Spring Boot app start karta hai.

### 4. Environment Precedence
- Real OS environment variables hamesha `.env` file se higher precedence rakhte hain. Agar system ya deployment server par OS env variable set hoga, toh wahi use hoga.