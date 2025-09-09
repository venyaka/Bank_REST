#!/bin/sh
set -e

sleep 5

vault kv put secret/card-encryption key="0123456789abcdef0123456789abcdef"

