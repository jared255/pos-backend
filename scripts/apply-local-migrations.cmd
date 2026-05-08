@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0apply-local-migrations.ps1" %*
