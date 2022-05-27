<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://passx.cuodex.net">
    <img src="https://passx.cuodex.net/assets/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">PassX - API</h3>

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

# PassX API
The JSON REST API for the PassX password manager by [CuodeX.net](https://cuodex.net)

### Compile by source
1. Clone the project `git clone https://github.com/DevOFVictory/PassX-API.git`
2. Check out requirements below
3. Build project with maven
4. Start api `java -jar target.jar`

### Requirements
- Setup working Java 17 JDK environment
- Setup MySQL database server on Port 3006 and db-name `passx` (or change it in `src/main/ressources/application.properties`)
- Create MySQL User Default: `passx:passx` with basic structure and data permission for database above

### Links
- Webclient: https://github.com/CuodeX/PassX-Webclient
- Mobile client: https://github.com/DevOFVictory/PassX-Mobile
- Hosted service: https://passx.cuodex.net
- More project information: https://cuodex.net/passx

### To Do's
- [x] Implement user authentication
- [x] Delete Account Endpoint
- [x] Correct Status Code
- [x] Encryption
- [x] Correct status code
- [x] Session System
- [x] Correct type check + null
- [x] Credential add/remove/edit
- [x] Add more debug messages
- [ ] CLI + Command System
- [ ] Docker Compose support

### Documentation
https://documenter.getpostman.com/view/18057854/UVyxQZPu
