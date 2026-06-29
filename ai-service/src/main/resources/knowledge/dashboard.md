# Dashboard

## What is the dashboard?
The dashboard is the first page you see after logging in. It provides an at-a-glance summary of key activity in the system.

## What information is shown on the dashboard?
The dashboard displays summary cards for recent activity, including daily patient counts, user counts, and payment summaries. The data refreshes automatically. If a summary card shows no data, it may mean there is no activity for the current day or that the relevant backend service is unavailable.

## Real-time notifications
The application supports real-time notifications delivered via WebSocket. When the WebSocket connection is active a notification indicator appears in the header. Notifications inform you of important events such as new patient registrations or billing updates. If you are not receiving notifications, check with your administrator that the WebSocket feature is enabled for your environment.
