@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0test-user-api.ps1" %*
