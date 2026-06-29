# Patient Management System — Overview

## What is the Patient Management System?
The Patient Management System is a web application for managing patients, users, billing, analytics, and translations in a healthcare environment. It is built with an Angular frontend and multiple Spring Boot backend services. All backend communication goes through a central gateway.

## How to navigate the application
After logging in you are taken to the Dashboard. The main navigation sidebar on the left gives access to all major sections: Dashboard, Users, and Resource Bundles. Additional sections such as Patients, Billing, and Analytics are available depending on your role and permissions.

## Roles and permissions
Access to features is controlled by roles and permissions assigned to your account. For example, the Resource Bundles section is only visible to users who have the MANAGE_RESOURCE_BUNDLES permission. If a menu item or page is not visible, your account may not have the required permission. Contact your system administrator to request additional permissions.

## Authentication modes
The system supports two login modes: JWT mode and OAuth2/Keycloak mode. In JWT mode you log in with a username and password directly in the application. In OAuth2 mode you are redirected to Keycloak for authentication using your organisation's single sign-on. The active mode is configured by the administrator and cannot be changed from the UI.

## Session and token expiry
Your session will expire after a period of inactivity. When this happens you will be redirected to the login page. Log in again to continue. In JWT mode the token expiry is managed by the application. In OAuth2 mode it is managed by Keycloak.

## Support chatbot
A support chatbot is available in the bottom-right corner of every page. Click the chat button to open it. You can ask the chatbot questions about how to use any feature in the application. To clear the conversation history click the trash icon inside the chat panel.
