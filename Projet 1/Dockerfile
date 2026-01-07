# 1. Base image (JDK installed)
FROM eclipse-temurin:17-jdk-jammy

# 2. Sets the working directory inside the container
WORKDIR /app

# 3. Copy source code into the container
COPY App.java .

# 4. Compiles the Java file
RUN javac App.java

# 5. Command executed when container starts
CMD ["java", "App"]

