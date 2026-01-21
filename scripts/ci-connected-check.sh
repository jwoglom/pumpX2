#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="${SCRIPT_DIR}/.."
cd "${REPO_ROOT}"

ADB_BIN="${ADB:-adb}"

"${ADB_BIN}" start-server >/dev/null 2>&1

device_serial="${ANDROID_SERIAL:-}"

wait_for_device() {
  if [[ -n "${device_serial}" ]]; then
    "${ADB_BIN}" -s "${device_serial}" wait-for-device
  else
    "${ADB_BIN}" wait-for-device
    device_serial=$("${ADB_BIN}" devices | awk 'NR>1 && $2 == "device" {print $1; exit}')
  fi
}

ensure_device_serial() {
  if [[ -z "${device_serial}" ]]; then
    device_serial=$("${ADB_BIN}" devices | awk 'NR>1 && $2 == "device" {print $1; exit}')
  fi
  if [[ -z "${device_serial}" ]]; then
    echo "Failed to detect an attached Android device." >&2
    exit 1
  fi
}

wait_for_package_service() {
  local max_attempts=60
  local attempt=1
  while (( attempt <= max_attempts )); do
    if "${ADB_BIN}" -s "${device_serial}" shell pm list packages >/dev/null 2>&1; then
      echo "Package manager service is available."
      return 0
    fi
    echo "Waiting for package manager service (attempt ${attempt}/${max_attempts})..."
    sleep 5
    (( attempt++ ))
  done
  echo "Package manager service did not become available after ${max_attempts} attempts." >&2
  "${ADB_BIN}" -s "${device_serial}" shell dumpsys package 2>/dev/null || true
  return 1
}

wait_for_device
ensure_device_serial
echo "Using device serial: ${device_serial}"
wait_for_package_service

./gradlew connectedCheck
