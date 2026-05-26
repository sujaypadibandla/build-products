#!/bin/bash
# Run this on a fresh Amazon Linux 2023 EC2 t2.micro instance
# Usage: ssh ec2-user@<EC2_IP> < ec2-setup.sh

# Install Docker
sudo dnf update -y
sudo dnf install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

# Install Docker Compose plugin
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# Install AWS CLI (already on Amazon Linux 2023)
# aws --version

# Create app directory
mkdir -p ~/microservices

# Create .env file (edit with your values)
cat > ~/microservices/.env << 'EOF'
DB_PASSWORD=onstar123
AWS_ACCOUNT_ID=your_aws_account_id
EOF

echo "=== Setup complete ==="
echo "1. Log out and back in (for docker group)"
echo "2. Edit ~/microservices/.env with your actual values"
echo "3. Copy docker-compose.prod.yml to ~/microservices/docker-compose.yml"
echo "4. Run: cd ~/microservices && docker compose up -d"

