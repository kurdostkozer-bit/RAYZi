#!/bin/bash

# Update and clear screen
sudo apt update && clear

# Update and clear screen
sudo apt update
sudo apt upgrade -y
clear

# Install Node.js
echo "
################################################
#                INSTALL NODEJS                #
################################################
"
sudo apt install curl -y
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
nvm install v18.20.2
node -v

# Install Nginx
echo "
################################################
#                INSTALL NGINX                 #
################################################
"
sudo apt install -y nginx
sudo systemctl status nginx
clear

# Install PM2
echo "
################################################
#                INSTALL PM2                   #
################################################
"
sudo apt install npm -y
npm install pm2 -g
clear

# Get public IP address
get_public_ip=$(wget -T 10 -t 1 -4qO- "http://ip1.dynupdate.no-ip.com/" || curl -m 10 -4Ls "http://ip1.dynupdate.no-ip.com/" | grep -m 1 -oE '^[0-9]{1,3}(\.[0-9]{1,3}){3}$')
read -p "Public IPv4 address / hostname [$get_public_ip]: " public_ip
until [[ -n "$get_public_ip" || -n "$public_ip" ]]; do
    echo "Invalid input."
    read -p "Public IPv4 address / hostname: " public_ip
done
[[ -z "$public_ip" ]] && public_ip="$get_public_ip"
clear

read -p "Your app name: " app_name

# Mongodb User Name
mongodbUser_name=$(echo "$app_name" | tr '[:upper:]' '[:lower:]' | tr -d ' ')
echo "Your mongodb user name formatted: $mongodbUser_name"

get_shared_secret_key="5TIvw5cpc0"
read -p "Shared Secret key [5TIvw5cpc0]: " shared_secret_key
[[ -z "$shared_secret_key" ]] && shared_secret_key="$get_shared_secret_key"

get_shared_jwt_secret="2FhKmINItB"
read -p "Shared Jwt Secret [2FhKmINItB]: " shared_jwt_secret
[[ -z "$shared_jwt_secret" ]] && shared_jwt_secret="$get_shared_jwt_secret"

clear

# Install MongoDB
echo "
################################################
#                INSTALL MONGODB               #
################################################
"
sudo apt install software-properties-common gnupg apt-transport-https ca-certificates -y
curl -fsSL https://pgp.mongodb.com/server-7.0.asc |  sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor
echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
sudo apt update
sudo apt install mongodb-org -y
mongod --version
sudo systemctl start mongod
sudo ss -pnltu | grep 27017
sudo systemctl enable mongod

# Wait for MongoDB to start
sleep 10

# Create admin user
mongosh <<EOF
use $mongodbUser_name
db.createUser({user:"admin", pwd:"dbadmin123", roles: [{ role: "userAdminAnyDatabase", db: "admin" }, { role: "readWrite", db: "$mongodbUser_name" }]})
exit
EOF

# Enable authentication in MongoDB
sudo sed -i '/#security:/a\security:\n  authorization: enabled' /etc/mongod.conf
sudo sed -i "s/bindIp: 127.0.0.1/bindIp: 127.0.0.1,$public_ip/" /etc/mongod.conf
sudo systemctl restart mongod

# Set up admin / application configuration
read -p "Your admin sub-domain (admin.yourdomain.com): " your_admin_sub_domain
clear

# Configure Nginx
echo "
################################################
#                CONFIGURE NGINX               #
################################################
"
sudo tee -a /etc/nginx/sites-available/default > /dev/null << EOF
server {
    server_name $your_admin_sub_domain www.$your_admin_sub_domain;
    client_max_body_size 300M;
    access_log /var/log/nginx/admin.access.log;
    error_log /var/log/nginx/admin.error.log;
    root /path;
    location / {
        proxy_pass http://localhost:5000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
        proxy_redirect off;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header X-Nginx-Proxy true;
    }
}
EOF
sudo systemctl restart nginx
sudo systemctl status nginx
clear

# Secure Nginx with Let's Encrypt 
echo "
################################################
#      Secure Nginx with Let's Encrypt         #
################################################
"
cd ../..
sudo snap install core; sudo snap refresh core
sudo apt remove certbot
sudo snap install --classic certbot
sudo systemctl reload nginx
sudo certbot --nginx -d $your_admin_sub_domain
clear

# Set up teenpatti configuration
read -p "Your teenpatti sub-domain (teenpatti.yourdomain.com): " your_teenpatti_sub_domain
clear

# Configure Nginx
echo "
################################################
#                CONFIGURE NGINX               #
################################################
"
sudo tee -a /etc/nginx/sites-available/default > /dev/null << EOF
server {
    server_name $your_teenpatti_sub_domain www.$your_teenpatti_sub_domain;
    client_max_body_size 300M;
    access_log /var/log/nginx/teenpatti.access.log;
    error_log /var/log/nginx/teenpatti.error.log;
    root /path;
    location / {
        proxy_pass http://localhost:5001;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
        proxy_redirect off;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header X-Nginx-Proxy true;
    }
}
EOF
sudo systemctl restart nginx
sudo systemctl status nginx
clear

# Secure Nginx with Let's Encrypt 
echo "
################################################
#      Secure Nginx with Let's Encrypt         #
################################################
"
cd ../..
sudo snap install core; sudo snap refresh core
sudo apt remove certbot
sudo snap install --classic certbot
sudo systemctl reload nginx
sudo certbot --nginx -d $your_teenpatti_sub_domain
clear

# Install teenpatti backend dependencies
echo "
################################################
#            INSTALL TEENPATTI BACKEND         #
################################################
"
cd /home/code/teenpatti/backend || exit
sudo mkdir -p "public"
npm install
cat > config.js << EOF
module.exports = {
  adminBaseURL: "https://$your_admin_sub_domain/",
  PORT: 5001,
  mongoDbConnectionString: "mongodb://admin:dbadmin123@$public_ip:27017/$mongodbUser_name",
};
EOF
cd /home/code/teenpatti/backend || exit
pm2 start index.js --name teenpatti

# Install teenpatti frontend dependencies and build
echo "
################################################
#        INSTALL TEENPATTI FRONTEND            #
################################################
"
cd /home/code/teenpatti/frontend/src || exit
cat > config.js << EOF
export const baseURL = "https://$your_teenpatti_sub_domain/";
export const adminBaseURL = "https://$your_admin_sub_domain/";
export const key = "$shared_secret_key";
EOF

cd .. || exit  # Navigate back to the frontend directory
npm install --f
npm run build
sudo mv /home/code/teenpatti/frontend/build/* /home/code/teenpatti/backend/public

# Set up roulettecasino configuration
read -p "Your roulettecasino sub-domain (roulettecasino.yourdomain.com): " your_roulettecasino_sub_domain
clear

# Configure Nginx
echo "
################################################
#                CONFIGURE NGINX               #
################################################
"
sudo tee -a /etc/nginx/sites-available/default > /dev/null << EOF
server {
    server_name $your_roulettecasino_sub_domain www.$your_roulettecasino_sub_domain;
    client_max_body_size 300M;
    access_log /var/log/nginx/roulettecasino.access.log;
    error_log /var/log/nginx/roulettecasino.error.log;
    root /path;
    location / {
        proxy_pass http://localhost:5002;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
        proxy_redirect off;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header X-Nginx-Proxy true;
    }
}
EOF
sudo systemctl restart nginx
sudo systemctl status nginx
clear

# Secure Nginx with Let's Encrypt 
echo "
################################################
#      Secure Nginx with Let's Encrypt         #
################################################
"
cd ../..
sudo snap install core; sudo snap refresh core
sudo apt remove certbot
sudo snap install --classic certbot
sudo systemctl reload nginx
sudo certbot --nginx -d $your_roulettecasino_sub_domain
clear

# Install roulettecasino backend dependencies
echo "
################################################
#            INSTALL CASINO BACKEND            #
################################################
"
cd /home/code/roulettecasino/backend || exit
sudo mkdir -p "public"
npm install
cat > config.js << EOF
module.exports = {
  adminBaseURL: "https://$your_admin_sub_domain/",
  PORT: 5002,
  mongoDbConnectionString: "mongodb://admin:dbadmin123@$public_ip:27017/$mongodbUser_name",
};
EOF
cd /home/code/roulettecasino/backend || exit
pm2 start index.js --name roulettecasino

# Install roulettecasino frontend dependencies and build
echo "
################################################
#             INSTALL CASINO FRONTEND          #
################################################
"
cd /home/code/roulettecasino/frontend/src || exit
cat > config.js << EOF
export const baseURL = "https://$your_roulettecasino_sub_domain/";
export const adminBaseURL = "https://$your_admin_sub_domain/";
export const key = "$shared_secret_key";
EOF

cd /home/code/roulettecasino/frontend || exit
npm install --f
npm run build
sudo mv /home/code/roulettecasino/frontend/build/* /home/code/roulettecasino/backend/public

# Set up ferrywheel configuration
read -p "Your ferrywheel sub-domain (ferrywheel.yourdomain.com): " your_ferrywheel_sub_domain
clear

# Configure Nginx
echo "
################################################
#                CONFIGURE NGINX               #
################################################
"
sudo tee -a /etc/nginx/sites-available/default > /dev/null << EOF
server {
    server_name $your_ferrywheel_sub_domain www.$your_ferrywheel_sub_domain;
    client_max_body_size 300M;
    access_log /var/log/nginx/ferrywheel.access.log;
    error_log /var/log/nginx/ferrywheel.error.log;
    root /path;
    location / {
        proxy_pass http://localhost:5003;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
        proxy_redirect off;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header X-Nginx-Proxy true;
    }
}
EOF
sudo systemctl restart nginx
sudo systemctl status nginx
clear

# Secure Nginx with Let's Encrypt 
echo "
################################################
#      Secure Nginx with Let's Encrypt         #
################################################
"
cd ../..
sudo snap install core; sudo snap refresh core
sudo apt remove certbot
sudo snap install --classic certbot
sudo systemctl reload nginx
sudo certbot --nginx -d $your_ferrywheel_sub_domain
clear

# Install ferrywheel backend dependencies
echo "
################################################
#            INSTALL FERRYWHEEL BACKEND        #
################################################
"
cd /home/code/ferrywheel/backend || exit
sudo mkdir -p "public"
npm install
cat > config.js << EOF
module.exports = {
  adminBaseURL: "https://$your_admin_sub_domain/",
  PORT: 5003,
  mongoDbConnectionString: "mongodb://admin:dbadmin123@$public_ip:27017/$mongodbUser_name",
};
EOF
cd /home/code/ferrywheel/backend || exit
pm2 start index.js --name ferrywheel

# Install ferrywheel frontend dependencies and build
echo "
################################################
#         INSTALL FERRYWHEEL FRONTEND          #
################################################
"
cd /home/code/ferrywheel/frontend/src || exit
cat > config.js << EOF
export const baseURL = "https://$your_ferrywheel_sub_domain/";
export const adminBaseURL = "https://$your_admin_sub_domain/";
export const key = "$shared_secret_key";
EOF

cd /home/code/ferrywheel/frontend || exit
npm install --f
npm run build
sudo mv /home/code/ferrywheel/frontend/build/* /home/code/ferrywheel/backend/public

# Set up agency configuration
read -p "Your agency sub-domain (agency.yourdomain.com): " your_agency_sub_domain
clear

# Configure Nginx
echo "
################################################
#                CONFIGURE NGINX               #
################################################
"
sudo tee -a /etc/nginx/sites-available/default > /dev/null << EOF
server {
    server_name $your_agency_sub_domain www.$your_agency_sub_domain;
    client_max_body_size 300M;
    access_log /var/log/nginx/$mongodbUser_name.access.log;
    error_log /var/log/nginx/$mongodbUser_name.error.log;
    root /path;
    location / {
        proxy_pass http://localhost:5004;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
        proxy_redirect off;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header X-Nginx-Proxy true;
    }
}
EOF
sudo systemctl restart nginx
sudo systemctl status nginx

# Secure Nginx with Let's Encrypt 
echo "
################################################
#      Secure Nginx with Let's Encrypt         #
################################################
"
cd ../..
sudo snap install core; sudo snap refresh core
sudo apt remove certbot
sudo snap install --classic certbot
sudo systemctl reload nginx
sudo certbot --nginx -d $your_agency_sub_domain
clear

cd /home/code/agency/backend || exit
sudo mkdir -p "public"
npm install
pm2 start index.js --name agency
pm2 status
node -v
pm2 restart agency --interpreter $(which node)

# Install agency frontend dependencies and build
echo "
################################################
#          INSTALL AGENCY FRONTEND             #
################################################
"
cd /home/code/agency/frontend/src/util || exit
cat > Config.js << EOF
export const baseURL = "https://$your_admin_sub_domain/";
export const key = "$shared_secret_key";
EOF

cd /home/code/agency/frontend || exit
npm install --f
npm run build
sudo mv /home/code/agency/frontend/build/* /home/code/agency/backend/public

# Set up host configuration
read -p "Your host sub-domain (host.yourdomain.com): " your_host_sub_domain
clear

# Configure Nginx
echo "
################################################
#                CONFIGURE NGINX               #
################################################
"
sudo tee -a /etc/nginx/sites-available/default > /dev/null << EOF
server {
    server_name $your_host_sub_domain www.$your_host_sub_domain;
    client_max_body_size 300M;
    access_log /var/log/nginx/$mongodbUser_name.access.log;
    error_log /var/log/nginx/$mongodbUser_name.error.log;
    root /path;
    location / {
        proxy_pass http://localhost:5005;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
        proxy_redirect off;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_set_header X-Nginx-Proxy true;
    }
}
EOF
sudo systemctl restart nginx
sudo systemctl status nginx

# Secure Nginx with Let's Encrypt 
echo "
################################################
#      Secure Nginx with Let's Encrypt         #
################################################
"
cd ../..
sudo snap install core; sudo snap refresh core
sudo apt remove certbot
sudo snap install --classic certbot
sudo systemctl reload nginx
sudo certbot --nginx -d $your_host_sub_domain
clear

cd /home/code/host/backend || exit
sudo mkdir -p "public"
npm install
pm2 start index.js --name host
pm2 status
node -v
pm2 restart host --interpreter $(which node)

# Install host frontend dependencies and build
echo "
################################################
#          INSTALL HOST FRONTEND               #
################################################
"
cd /home/code/host/frontend/src/util || exit
cat > Config.js << EOF
exports.baseURL = "https://$your_admin_sub_domain/";
exports.key = "$shared_secret_key";
exports.projectName = "$app_name"
EOF

cd /home/code/host/frontend || exit
npm install --f
npm run build
sudo mv /home/code/host/frontend/build/* /home/code/host/backend/public

# Install backend dependencies
echo "
################################################
#                INSTALL BACKEND               #
################################################
"
cd /home/code/admin/backend || exit
sudo mkdir -p "public"
npm install
cat > config.js << EOF
module.exports = {
  //Port
  PORT: 5000,

  //Project Name
  projectName : "$app_name",

  //Gmail credentials for send email
  EMAIL: "kodebookapp@gmail.com",
  PASSWORD: "nohwrpybgiuhqjfy",

  //Secret key for jwt
  JWT_SECRET: "$shared_jwt_secret",

  //Secret key for API
  secretKey: "$shared_secret_key",

  //baseURL
  baseURL: "https://$your_admin_sub_domain/",

  //agency path
  AGENCY_PATH: "https://$your_agency_sub_domain/",

  //host path
  HOST_PATH: "https://$your_host_sub_domain/",

  //Mongodb string
  MongoDb_Connection_String: "mongodb://admin:dbadmin123@$public_ip:27017/$mongodbUser_name"
};
EOF
cd /home/code/admin/backend || exit
pm2 start index.js --name backend

# Install admin frontend dependencies and build
echo "
################################################
#                INSTALL FRONTEND              #
################################################
"
cd /home/code/admin/frontend/src/util || exit
cat > Config.js << EOF
exports.baseURL = "https://$your_admin_sub_domain/";
exports.key = "$shared_secret_key";
exports.projectName = "$app_name"
EOF
npm install --f
npm run build
sudo mv /home/code/admin/frontend/build/* /home/code/admin/backend/public

echo "
################################################
#                CONGRATULATIONS!              #
################################################
Server setup is complete.
1. baseURL : https://$your_admin_sub_domain/
2. Secret key : $shared_secret_key
3. MONGODB_CONNECTION_STRING: "mongodb://admin:dbadmin123@${public_ip}:27017/${mongodbUser_name}"
4. Admin Panel: https://$your_admin_sub_domain/
5. Ferrywheel Game: https://$your_ferrywheel_sub_domain/
6. TeenPatti Game: https://$your_teenpatti_sub_domain/
7. Roulettecasino Game: https://$your_roulettecasino_sub_domain/
"