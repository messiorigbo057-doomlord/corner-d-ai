# Phone-friendly deployment

You can manage the deployment from an Android phone. You do NOT need Android Studio on the phone.

## Part 1 — Deploy the backend

Use a cloud Node.js host that supports the included `server/render.yaml`.

1. Create a GitHub repository from this project.
2. Upload the project ZIP contents to the repository.
3. Create a new Web Service from the repository.
4. Use the `server` directory as the service root.
5. Build command: `npm install`
6. Start command: `npm start`
7. Add the secret environment variable:
   `REPLICATE_API_TOKEN = your own provider token`
8. Deploy.
9. Open:
   `https://YOUR-BACKEND/health`
   You should receive:
   `{"ok":true}`

## Part 2 — Put your backend URL into the Android app

Open:

`app/src/main/java/com/cornerd/vidforgeai/VideoViewModel.kt`

Change:

`https://YOUR-BACKEND.example.com`

to your real HTTPS backend URL.

Commit the change.

## Part 3 — Build the APK from your phone

The repository contains:

`.github/workflows/build-apk.yml`

After pushing to GitHub:

1. Open the repository in your phone browser.
2. Open the Actions tab.
3. Run `Build VidForge APK`.
4. Wait for the workflow to finish.
5. Open the completed workflow.
6. Download the `VidForge-debug-apk` artifact.
7. Extract the APK.
8. Install it on your Android phone.

For a personal test build, a debug APK is enough. A release APK should be signed with a private key before wider distribution.

## Important

The APK never contains the provider token. The token belongs only in the backend's environment variables.

Cloud video generation still consumes the provider's normal usage/credits. This project does not bypass those controls.
