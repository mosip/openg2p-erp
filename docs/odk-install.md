# ODK Installation and Setup
ODK lets you build powerful forms to collect the data you need wherever it is.
Central is the [ODK](https://getodk.org/) server. It manages user accounts and permissions, stores form definitions, and allows data collection clients like ODK Collect to connect to it for form download and submission upload.

The installation procedure here is specifically targeting Linux (Ubuntu).
## Installing ODK Central

ODK Central installation process will involve below tasks:
1.  Obtain a domain name and setting up your server with ssl
2.  Install ODK Central
3.  Running ODK Central

### 1. Obtain a domain name and setting up your server with ssl

ODK Central demands that you should have a well qualified domain name and ssl for atleast few of its components like **Enketo** and the **Collect** mobile app.


### 2. Install ODK Central

#### Setup Docker
First, you'll need to upgrade to docker-compose v1.28.3 or later. Follow these commands from [Docker's documentation](https://docs.docker.com/compose/install/#install-compose-on-linux-systems).

	
```
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
```
	
Next, you would want to ensure that Docker starts up whenever the server starts. Docker will in turn ensure that Central has started up. To do this, run
```
systemctl enable docker.	
```

#### UFW Configuration

1. For a quick setup you may try out the disable ufw like below,
	```
	ufw disable
	```

2. If you keep the ufw running, please do ensure that the NGINX is given full access. 
	```
	sudo ufw allow 'Nginx HTTP'

	```
3. If you don't want to disable the firewall entirely, you can instead configure Docker, **iptables**, and **ufw** yourself.  Another option is to use an upstream network firewall.


#### Getting and Setting Up ODK Central

Now you'll need to download the software. 

1. In the server window, execute 
	```
	git clone https://github.com/getodk/central 
	```

2. Move into the directory
	```
	cd central
	```
 

3. Perform submodule update to fetch the submodules involved. 
	``` 
	git submodule update -i 
	```

#### Configure ODK Central

To configure **ODK Central**, you may copy the template file and edit it.
```
mv .env.template .env
nano .env
```

Change the `DOMAIN` line so that after the `=` is the domain name you registered above. As an example: `DOMAIN=odk.somewhere.com`. Do not include anything like *http://*

Change the `SYSADMIN_EMAIL` line so that after the `=` is your own email address. The Let's Encrypt service will use this address only to notify you if something is wrong with your security certificate.

Leave the rest of the settings alone. If you have a custom security or network environment you are trying to integrate ODK Central into, see the [advanced configuration](https://docs.getodk.org/central-install-digital-ocean/#central-install-digital-ocean-advanced) sections for more information on these options.

Hold `Ctrl + x` to quit the text editor. Press `y` to indicate that you want to save the file, and then press Enter to confirm the file name. Do not change the file name.

### 3. Running ODK Central


#### Start ODK Central Software
To start the server software please execute
```
docker-compose up -d 
```

The first time you start it, it will take a while to set itself up. Once you give it a few minutes and you have input control again, you'll want to see whether everything is running correctly. To do so, you may run 
```
docker-compose ps
```

Under the `State` column, for the nginx row, you will want to see text that reads `Up` or `Up (healthy)`. If you see `Up (health: starting)`, give it a few minutes. If you see some other text, something has gone wrong. It is normal to see `Exit 0` for the `secrets` container.


If your domain name has started working, you can visit it in a web browser to check that you get the ODK Central management website.

#### Setup User

Ensure that you are in the `central` folder on your server. If you have not closed your console session from earlier, you should be fine. If you have just logged back into it, you'll want to run `cd central` to navigate to that folder.

- Run bellow command to create a user in ODK Central

	```
	docker-compose exec service odk-cmd --email YOUREMAIL@ADDRESSHERE.com user-create
	```
	Do substitute your email address as appropriate. Press Enter, and you will be asked for a password for this new account.

- To give administrator previlege to the user, run as below 
	```
	docker-compose exec service odk-cmd --email YOUREMAIL@ADDRESSHERE.com user-promote
	``` 

- To reset password, you may use the below command 
	```
	docker-compose exec service odk-cmd --email YOUREMAIL@ADDRESSHERE.com user-set-password
	``` 
	As with account creation, you will be prompted for a new password after you press Enter.


#### Using a Custom SSL Certificate
By default, ODK Central uses Let's Encrypt to obtain an SSL security certificate. For most users, this should work perfectly, but larger managed internal networks may have their own certificate trust infrastructure. To use your own custom SSL certificate rather than the automatic Let's Encrypt system:
1. Generate a `fullchain.pem` (`-out`) file which contains your certificate followed by any necessary intermediate certificate(s).
2. Generate a privkey.pem (-keyout) file which contains the private key used to sign your certificate.
3. Copy those files into `files/local/customssl/` within the repository root.
4. In `.env`, set `SSL_TYPE` to `customssl` and set `DOMAIN` to the domain name you registered. As an example: `DOMAIN=MyOdkCollectionServer.com`. Do not include anything like `http://`.
5. Build and run: `docker-compose build nginx`, `docker-compose stop nginx`, `docker-compose up -d nginx`. If that doesn't work, you may need to first remove your old nginx container (`docker-compose rm nginx`).


## ODK Collect

In case you want to try out on the ODK flow, you may goahead and install the mobile app [ODK Collect](https://play.google.com/store/apps/details?id=org.odk.collect.android&hl=en_IN&gl=US)

Alternately, you can clone [Collect](https://github.com/getodk/collect) code. 
```
git clone https://github.com/getodk/collect
```

The code can be opened in [Android Studio](https://developer.android.com/studio).
