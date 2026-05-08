@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0init-local-postgres.ps1" %*
