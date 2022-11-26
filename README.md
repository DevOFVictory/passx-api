<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://passx.cuodex.net">
    <img src="https://passx.cuodex.net/assets/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">PassX - API v3.2</h3>

  <p align="center">
    A safe place for your passwords from everywhere
    <br />
    <a href="https://passx.cuodex.net"><strong>Go to the Webinterface »</strong></a>
    <br />
    <br />
    <a href="https://cuodex.net/passx">View Website</a>
    ·
    <a href="https://cuodex.net/contact#reportBug">Report Bug</a>
  </p>
</div>

# PassX API 3
The JSON REST API for the PassX password manager by [CuodeX.net](https://cuodex.net)

### Features
- Server side encryption
- Two-factor authentication via TOTP
- Remember current IP address for further logins
- Generate TOTP QR Codes
- Check for most common passwords in common databases
- Check current status and more information on the api
- Built-in bruteforce protection via IP block
- Detailed log for administrators to check uncommon behaviour
- Security & privacy granted by FOSS and non-profit project
- Detailed exceptions and documentation
- Easy to use and understand
- Easy to integrate into your own projects
- Custom settings for each user (can be used for custom frontend appearance)



### Compile by source
1. Clone the project `git clone https://github.com/DevOFVictory/PassX-API.git`
2. Check out requirements below
3. Build project with maven ``mvn package``
4. Start api `java -jar target.jar`

### Requirements
- Setup working Java 17 JDK environment
- Setup MySQL database server on Port 3006 and db-name `passx` (or change it in `src/main/ressources/application.properties`)

### Links
- Webclient: https://github.com/CuodeX/PassX-Webclient
- Mobile client: https://github.com/DevOFVictory/PassX-Mobile
- Hosted service: https://passx.cuodex.net
- More project information: https://cuodex.net/passx


### Documentation
https://documenter.getpostman.com/view/18057854/UVyxQZPu
