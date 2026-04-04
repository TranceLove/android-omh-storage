package com.openmobilehub.android.storage.plugin.box.restful.data.source.mapper

import com.openmobilehub.android.storage.core.model.OmhCreatePermission
import com.openmobilehub.android.storage.core.model.OmhIdentity
import com.openmobilehub.android.storage.core.model.OmhPermission
import com.openmobilehub.android.storage.core.model.OmhPermissionRecipient
import com.openmobilehub.android.storage.core.model.OmhPermissionRole
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CollaborationAccessibleBy
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CollaborationItem
import com.openmobilehub.android.storage.plugin.box.restful.data.source.body.CreateCollaborationRequest
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxCollaboration
import com.openmobilehub.android.storage.plugin.box.restful.data.source.response.BoxCollaborator
import java.time.ZonedDateTime
import java.util.Date

// Convert Box collaboration to OMH permission
fun BoxCollaboration.toOmhPermission(): OmhPermission.IdentityPermission {
    return OmhPermission.IdentityPermission(
        id = id,
        role = role.toOmhPermissionRole(),
        isInherited = false, // Box collaborations are direct, not inherited
        identity = accessibleBy?.toOmhIdentity() ?: OmhIdentity.User(
            id = null,
            displayName = inviteEmail,
            emailAddress = inviteEmail,
            expirationTime = expiresAt?.let { parseBoxDateTimeToDate(it) },
            deleted = false,
            photoLink = null,
            pendingOwner = role == "owner" && status == "pending"
        )
    )
}

// Convert Box collaborator to OMH identity
fun BoxCollaborator.toOmhIdentity(): OmhIdentity {
    return when (type) {
        "user" -> OmhIdentity.User(
            id = id,
            displayName = name,
            emailAddress = login,
            expirationTime = null, // Box doesn't provide user expiration in collaborator
            deleted = false,
            photoLink = null,
            pendingOwner = false
        )
        "group" -> OmhIdentity.Group(
            id = id,
            displayName = name,
            emailAddress = null, // Box groups don't have email addresses
            expirationTime = null,
            deleted = false
        )
        else -> throw IllegalArgumentException("Unknown Box collaborator type: $type")
    }
}

// Convert Box role to OMH role
fun String.toOmhPermissionRole(): OmhPermissionRole {
    return when (this) {
        "owner", "co-owner" -> OmhPermissionRole.OWNER
        "editor" -> OmhPermissionRole.WRITER
        "viewer", "viewer uploader", "previewer", "previewer uploader", "uploader" -> OmhPermissionRole.READER
        else -> OmhPermissionRole.READER // Default to reader for unknown roles
    }
}

// Convert OMH role to Box role
fun OmhPermissionRole.toBoxRole(): String {
    return when (this) {
        OmhPermissionRole.OWNER -> "co-owner" // Use co-owner as it's safer than owner
        OmhPermissionRole.WRITER -> "editor"
        OmhPermissionRole.COMMENTER -> "editor" // Box doesn't have a pure commenter role
        OmhPermissionRole.READER -> "viewer"
    }
}

// Convert OMH create permission to Box collaboration request
fun OmhCreatePermission.CreateIdentityPermission.toCreateCollaborationRequest(
    fileId: String,
    itemType: String // "file" or "folder"
): CreateCollaborationRequest {
    return CreateCollaborationRequest(
        item = CollaborationItem(
            id = fileId,
            type = itemType
        ),
        accessibleBy = recipient.toCollaborationAccessibleBy(),
        role = role.toBoxRole()
    )
}

// Convert OMH recipient to Box accessible by
fun OmhPermissionRecipient.toCollaborationAccessibleBy(): CollaborationAccessibleBy {
    return when (this) {
        is OmhPermissionRecipient.User -> CollaborationAccessibleBy(
            type = "user",
            login = emailAddress
        )
        is OmhPermissionRecipient.Group -> CollaborationAccessibleBy(
            type = "group",
            login = emailAddress
        )
        is OmhPermissionRecipient.WithObjectId -> CollaborationAccessibleBy(
            type = "user", // Assume user for object ID
            id = id
        )
        else -> throw IllegalArgumentException("Unsupported recipient type for Box: $this")
    }
}

private fun parseBoxDateTimeToDate(dateTimeString: String): Date {
    val zonedDateTime = ZonedDateTime.parse(dateTimeString)
    return Date.from(zonedDateTime.toInstant())
}
