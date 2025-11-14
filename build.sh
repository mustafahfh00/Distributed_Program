#!/bin/bash
# ====================================================================
# SE355 – Assignment 1
# Distributed Word Delivery System (7 processes)
#
# Authors:
#   Mustafa Haitham Fadhil <mh22197@auis.edu.krd>
#   Hazhin Noori Mahmood <hn22045@auis.edu.krd>
# Instructor: Yad Tahir
# ====================================================================

set -e

SRC="./src"
BIN="./bin"
PKG="assignment1"
LAUNCH="RunAll"

echo "🧹 Cleaning..."
rm -rf "$BIN"
mkdir -p "$BIN"

echo "🔧 Compiling Java files..."
find "$SRC" -name "*.java" > sources.txt
javac -d "$BIN" @sources.txt
rm sources.txt
echo "✅ Build complete."

echo "🚀 Running $PKG.$LAUNCH"
java -cp "$BIN" "$PKG.$LAUNCH"
