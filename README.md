
<h1 align="center">GreenStreak</h1>

<div align="center">

[![GitHub](https://img.shields.io/badge/GitHub-%23121011.svg?logo=github&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff)](#)
[![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=fff)](#)
[![React](https://img.shields.io/badge/React-%2320232a.svg?logo=react&logoColor=%2361DAFB)](#)

<div align="center">Because your GitHub profile deserves to be as green as a summer meadow!</div>

</div>

## About

A GitHub contribution automation tool that lets you schedule commits with custom patterns. Built with Spring Boot and React, featuring Quartz scheduler for reliable timing control. 
Deploy GreenStreak on your server in no time—just run single command


## Quick Start

```bash
  docker run -d -p 8081:8081 emirakts/green-streak
```

<table>
  <tr>
    <td>🌐 Web Interface Address</td>
    <td><a href="http://localhost:8081">http://localhost:8081</a></td>
  </tr>
  <tr>
    <td><strong>Username</strong></td>
    <td><code>green</code></td>
  </tr>
  <tr>
    <td><strong>Password</strong></td>
    <td><code>green</code></td>
  </tr>
</table>


> ⚠️ **Important:** Please change these credentials immediately after your first login.
>
> 🛠️ **Tip:** Use Docker Compose for env configuration.

## Configuration

1. Generate a GitHub Personal Access Token (with minimal repo permissions).
2. Enter your details (GitHub Username, Token, etc.).
3. Set your commit frequency and click **Save Schedule**.
4. That’s it—GreenStreak will do the rest!


## Some Screenshots

<div >
  <img src="images/fe-image- (1).png" alt="Login" width="400"/>
  <img src="images/fe-image- (2).png" alt="Dashboard 1" width="400"/>
  
</div>

<div >
  <img src="images/fe-image- (3).png" alt="schedule" width="400"/>
  <img src="images/fe-image- (4).png" alt="Dashboard 2" width="400"/>
</div>

<div >
  <img src="images/fe-image- (10).png" alt="cancel" width="400"/>
  <img src="images/fe-image- (8).png" alt="history 2 Selection" width="400"/>
</div>


## FAQ

| **Question** | **Answer** |
|--------------|------------|
| **I'm going to provide my token, but how can I trust it's secure?** | It only accesses your selected, non-critical repo. Use a token with *minimal permissions*. |
| **Is this cheating?** | I prefer to call it *"gardening automation."* |
| **Will this make me a better developer?** | **No**, but it will make your profile look like one! *(Just kidding—real skills come from real coding.)* |


## Disclaimer

This tool is meant for educational and demonstration purposes. Use it responsibly and in accordance with GitHub's terms of service. Remember, quality contributions are always better than quantity! 

#

<div align="center">

[![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome)

</div>

#
