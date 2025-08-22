# =========================================================
# Script PowerShell: Clean, Build e Install APK Android
# =========================================================

# -------------------------------
# Configurazioni
# -------------------------------
$projectPath = "C:\Users\marya\UniProjects\IUM\ImmunoBuddy"  # Percorso del progetto
$apkPath = "$projectPath\app\build\outputs\apk\debug\app-debug.apk"
$packageName = "com.tuo.pacchetto"  # Sostituire con il package name reale

# -------------------------------
# 1. Chiudere Android Studio e processi Java/Gradle
# -------------------------------
Write-Host "Chiudendo eventuali processi Java/Gradle..."
Get-Process java, gradle -ErrorAction SilentlyContinue | Stop-Process -Force

# -------------------------------
# 2. Pulizia della cartella build
# -------------------------------
Write-Host "Rimuovendo cartella build..."
$buildDir = "$projectPath\app\build"
if (Test-Path $buildDir) {
    Remove-Item -Recurse -Force $buildDir
    Write-Host "Cartella build rimossa con successo."
} else {
    Write-Host "Cartella build non esiste, procedo..."
}

# -------------------------------
# 3. Compilazione APK
# -------------------------------
Write-Host "Compilando APK..."
cd $projectPath
.\gradlew.bat assembleDebug --no-daemon --refresh-dependencies

if (!(Test-Path $apkPath)) {
    Write-Host "Errore: APK non generato. Controllare eventuali errori di compilazione."
    exit 1
}

# -------------------------------
# 4. Pulizia dati app sul dispositivo
# -------------------------------
Write-Host "Pulendo dati residui dell'app sul dispositivo..."
adb shell pm clear $packageName

# -------------------------------
# 5. Installazione APK sul dispositivo
# -------------------------------
Write-Host "Installando APK sul dispositivo..."
adb install -r $apkPath

Write-Host "Build e installazione completate con successo!"
