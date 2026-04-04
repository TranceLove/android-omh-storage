Module plugin-box-restful

<p align="center">
  <a href="https://openmobilehub.github.io/android-omh-storage/docs/">
    <img width="500px" src="https://openmobilehub.org/wp-content/uploads/sites/13/2024/06/OpenMobileHub-horizontal-color.svg"/><br/>
  </a>
  <h2 align="center">Android OMH Storage - Box (RESTful)</h2>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/com.openmobilehub.android.storage/plugin-box-restful"><img src="https://img.shields.io/maven-central/v/com.openmobilehub.android.storage/plugin-box-restful" alt="NPM version"/></a>
  <a href="https://github.com/openmobilehub/android-omh-storage/blob/main/LICENSE"><img src="https://img.shields.io/github/license/openmobilehub/android-omh-storage" alt="License"/></a>
</p>

<p align="center">
  <a href="https://discord.com/invite/yTAFKbeVMw"><img src="https://img.shields.io/discord/1115727214827278446.svg?style=flat&colorA=7289da&label=Chat%20on%20Discord" alt="Chat on Discord"/></a>
  <a href="https://twitter.com/openmobilehub"><img src="https://img.shields.io/twitter/follow/rnfirebase.svg?style=flat&colorA=1da1f2&colorB=&label=Follow%20on%20Twitter" alt="Follow on Twitter"/></a>
</p>

---

Box Implementation of OMH Storage API using Box's own HTTP REST-ful API.

This plugin provides a RESTful implementation for Box cloud storage, enabling seamless integration with the OMH Storage framework without depending on Box's native SDK.

## Features

- **File Operations**: Upload, download, delete, and manage files
- **Folder Operations**: Create, delete, and manage folders
- **Search**: Search for files and folders
- **Metadata**: Retrieve storage usage and quota information
- **File Metadata**: Get detailed metadata for specific files
- **Web URLs**: Get web URLs for files (shared links)
- **Path Resolution**: Resolve file/folder paths to entities
- **Authentication**: OAuth 2.0 authentication flow
- **Chunked Upload**: Automatic chunked upload for files ≥20MB (Box requirement)

## Usage

### Set up your Box application

1. Go to the [Box Developer Console](https://app.box.com/developers/console)
2. Create a new application or select an existing one
3. Configure OAuth 2.0 settings:
   - Set redirect URI for your application
   - Note down your Client ID and Client Secret
4. Configure scopes according to your needs

### Authentication

This plugin uses OAuth 2.0 authentication. You'll need to:

1. Configure your Box application with appropriate redirect URIs
2. Implement the OAuth flow in your application
3. Provide the access token to the OMH Storage client

### Integration

```kotlin
val authClient = // Your OmhAuthClient implementation
val storageClient = BoxRestfulOmhStorageClientFactory().getStorageClient(authClient)

// List files in root folder
val files = storageClient.listFiles("0")

// Upload a file
val uploadedFile = storageClient.uploadFile(localFile, "0")

// Create a folder
val newFolder = storageClient.createFolder("My Folder", "0")

// Get a file thumbnail
val thumbnail = storageClient.getFileThumbnail("file_id", ThumbnailSize.MEDIUM)

// Upload large file (automatically uses chunked upload for files ≥20MB)
val largeFile = File("/path/to/large/file.zip") // e.g., 50MB file
val uploadedFile = storageClient.uploadFile(largeFile, "0") // Automatically uses chunked upload

// Get file versions (requires premium Box account)
val fileVersions = storageClient.getFileVersions("file_id")
fileVersions.forEach { version ->
    println("Version ${version.versionId}: ${version.name} (${version.size} bytes)")
}

// Download specific file version
val versionContent = storageClient.downloadFileVersion("file_id", "version_id")

// File permissions (collaborations)
// Create permission - invite user to collaborate
val createPermission = OmhCreatePermission.CreateIdentityPermission(
    role = OmhPermissionRole.WRITER,
    recipient = OmhPermissionRecipient.User("user@example.com")
)
val newPermission = storageClient.createPermission("file_id", createPermission)

// Get all permissions for a file
val permissions = storageClient.getFilePermissions("file_id")
permissions.forEach { permission ->
    println("Permission ${permission.id}: ${permission.role}")
}

// Update permission role
storageClient.updatePermission("file_id", "permission_id", OmhPermissionRole.READER)

// Delete permission
storageClient.deletePermission("file_id", "permission_id")

// Get storage quota and usage
val quota = storageClient.getStorageQuota() // Total space in bytes
val usage = storageClient.getStorageUsage() // Used space in bytes

// Get file metadata
val fileMetadata = storageClient.getFileMetadata("file_id")
println("File: ${fileMetadata?.entity}")

// Get web URL for a file (shared link)
val webUrl = storageClient.getWebUrl("file_id")
println("Web URL: $webUrl")

// Resolve a file/folder path
val entity = storageClient.resolvePath("/Documents/Report.pdf")
when (entity) {
    is OmhStorageEntity.OmhFile -> println("Found file: ${entity.name}")
    is OmhStorageEntity.OmhFolder -> println("Found folder: ${entity.name}")
    null -> println("Path not found")
}
```

### Configuration

Add the following to your `gradle.properties` or environment variables:

```properties
boxApiUrl="https://api.box.com/2.0/"
boxUploadApiUrl="https://upload.box.com/api/2.0/"
```

## Supported Operations

- ✅ List files and folders
- ✅ Upload files
- ✅ Download files
- ✅ Create folders
- ✅ Delete files and folders
- ✅ Search files and folders
- ✅ Get storage metadata
- ✅ Update file/folder names
- ✅ File thumbnails (PNG/JPG formats, multiple sizes)
- ✅ Chunked upload for large files (≥20MB automatically)
- ✅ File versions (list and download specific versions)
- ✅ File permissions (collaborations - create, update, delete)
- ❌ File export with different MIME types

## API Limitations

- Files smaller than 20MB use direct upload, files ≥20MB use chunked upload (Box requirement)
- Chunked uploads use 8MB chunks for optimal performance
- File versions are only available for Box users with premium accounts
- File permissions are managed through Box collaborations (user/group access control)
- Some advanced Box features like file export with different MIME types are not implemented in this version

## Error Handling

The plugin uses the standard OMH Storage exception handling:

- `OmhStorageException.ApiException` for API-related errors
- Network timeouts and connectivity issues are handled by the underlying HTTP client

### Caveats

- File creation with specific MIME types or extensions is not supported - use `uploadFile` instead
- Advanced Box features like file versioning, thumbnails, and permissions are not implemented
- Large file uploads (>50MB) require chunked upload implementation

### Escape Hatch

This plugin does not provide an escape hatch to access the native Box SDK, as it uses REST API instead. If needed, you can use credentials from OmhAuthClient to authorize your own REST API client.

## License

- See [LICENSE](https://github.com/openmobilehub/android-omh-storage/blob/main/LICENSE)