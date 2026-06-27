#!/bin/bash
# Full setup after a reboot — /tmp is cleared on restart so Maven and the
# Java compiler stub must be recreated before anything can build or run.
cd "$(dirname "$0")"

# ── 1. Install Maven to /tmp ──────────────────────────────────────────────────
if [ ! -f /tmp/apache-maven-3.9.5/bin/mvn ]; then
    echo "Installing Maven to /tmp..."
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz \
         -o /tmp/mvn.tar.gz
    tar -xzf /tmp/mvn.tar.gz -C /tmp/
    rm /tmp/mvn.tar.gz
else
    echo "Maven already present."
fi
export PATH="/tmp/apache-maven-3.9.5/bin:$PATH"

# ── 2. Build java.lang.Compiler stub (removed in Java 17+) ───────────────────
if [ ! -f /tmp/compiler-stub.jar ]; then
    echo "Building compiler stub..."
    mkdir -p /tmp/stub/java/lang /tmp/stub-compiled
    cat > /tmp/stub/java/lang/Compiler.java << 'EOF'
package java.lang;
public final class Compiler {
    private Compiler() {}
    public static boolean compileClass(Class<?> c) { return false; }
    public static boolean compileClasses(String s) { return false; }
    public static Object command(Object o) { return null; }
    public static void enable() {}
    public static void disable() {}
}
EOF
    javac --patch-module java.base=/tmp/stub \
          -d /tmp/stub-compiled \
          /tmp/stub/java/lang/Compiler.java
    jar cf /tmp/compiler-stub.jar -C /tmp/stub-compiled .
else
    echo "Compiler stub already present."
fi

# ── 3. Kill any leftover server ───────────────────────────────────────────────
echo "Stopping any server on port 8080..."
fuser -k 8080/tcp &>/dev/null; sleep 1

# ── 4. Build and start ────────────────────────────────────────────────────────
echo "Building..."
mvn clean package -q
if [ $? -ne 0 ]; then
    echo "Build FAILED. Check output above."
    exit 1
fi

echo "Starting server at http://localhost:8080"
java --patch-module java.base=/tmp/compiler-stub.jar \
     -jar target/ne-ljuti-se-1.0-SNAPSHOT-web.jar
