# STRATO MERCATA

A mobile application for gold trading and asset management on the STRATO blockchain platform.

## Setup

### Credentials Configuration

The app requires credentials to connect to the STRATO blockchain API. These credentials are stored in a YAML file in the Android assets directory.

1. Navigate to `android/app/src/main/assets/`
2. Copy `credentials.template.yaml` to `credentials.yaml`
3. Edit `credentials.yaml` with your actual credentials:
   ```yaml
   # OAuth client credentials
   clientUrl: "your-strato-instance.blockapps.net"
   clientId: "your-client-id"
   clientSecret: "your-client-secret"
   
   # User common name for asset lookup
   userCommonName: "your-common-name"
   ```

**Important**: The `credentials.yaml` file contains sensitive information and should not be committed to version control. It is included in the `.gitignore` file by default. The `credentials.template.yaml` file is provided as a reference and contains mock values.

## Features

- Real-time asset tracking
- Gold price monitoring
- Trading simulation
- Portfolio management

## Development

### Running the App

```bash
# Install dependencies
npm install

# Start the Metro bundler
npm start

# Run on Android
npm run android
```

## Network Permissions

The app requires internet access to connect to the STRATO blockchain API. This permission is declared in the AndroidManifest.xml file.
