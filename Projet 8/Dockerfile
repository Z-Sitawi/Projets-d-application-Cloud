FROM eclipse-temurin:17-jdk

WORKDIR /app

# 1. Copy JADE library and Agent code
COPY jade-4.5.0.jar /app/jade.jar
COPY agents /app/agents

# 2. Compile the ResourceAgent
RUN javac -cp jade.jar agents/ResourceAgent.java

# 3. Set Classpath so we can run it easily later
ENV CLASSPATH=".:/app/jade.jar"

# Default command
CMD ["java", "jade.Boot", "-gui"]