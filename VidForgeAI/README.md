# CORNER-D AI — Android AI Video Studio


This is a standalone Android AI-video app. It does not modify or bypass Runway.

## Architecture

Android app → your HTTPS backend → Replicate → Seedance 1 Lite → video URL → Android app

The provider token stays on the backend.

## Current real features

- Text-to-video generation
- 5s / 10s
- 480p / 720p backend support
- 16:9 / 9:16 / 1:1
- Async generation
- Automatic status polling from Android
- Project/history entries
- Result URL returned to the app
- Basic backend validation
- `/health` endpoint

Seedance 1 Lite currently documents text-to-video and image-to-video, 5s/10s generation, 480p/720p, and the input fields used here.

## Backend setup

You need your own Replicate account/API token.

```bash
cd server
npm install
export REPLICATE_API_TOKEN="YOUR_TOKEN"
npm start
```

For a phone-only workflow, deploy the `server` directory to a cloud Node.js host that gives you an HTTPS URL. Put that URL into:

`app/src/main/java/com/cornerd/vidforgeai/VideoViewModel.kt`

Change:

`https://YOUR-BACKEND.example.com`

to your real backend URL.

## Build/install

Open the project in Android Studio and build an APK. For a release APK use:
Build → Generate Signed Bundle / APK → APK.

The APK cannot contain your Replicate token. Anyone who obtains an APK containing a secret can extract it, so the token remains server-side.

## Important

Cloud video generation is not unlimited/free. The model provider charges for compute according to its pricing. This project connects to the provider legitimately; it does not bypass credits, subscriptions, or payment controls.

## Next production upgrades

- Real image picker + image-to-video upload
- User authentication
- Per-user rate limits
- Persistent project database
- Video download/save-to-gallery
- In-app video player
- Webhook completion instead of polling
- Automatic deletion/retention policy
- Abuse/moderation controls


## Phone-friendly deployment

The project includes:
- `.github/workflows/build-apk.yml` to build the Android APK in GitHub Actions.
- `server/render.yaml` for a cloud Node.js deployment.
- `PHONE_DEPLOYMENT.md` with the phone-only workflow.

You still need a cloud account/provider token and GitHub account. GitHub Actions builds the APK remotely; you do not need Android Studio installed on your phone.


## Branding
The Android app display name is **CORNER-D AI**.
