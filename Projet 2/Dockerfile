# Official Node image
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

# Install dependencies
RUN npm install --production

# Copy the rest of the code
COPY src ./src

# Expose port
EXPOSE 3000

# Start command
CMD ["npm", "start"]

