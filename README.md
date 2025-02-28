# What is this mod?
**Dimension Blocker** is a **server-side mod** that adds **new commands** to block access to dimensions on your server!
This mod only block access through portals and teleportation but not with /execute, so admins have still a way to teleport to restricted dimensions.

# What new commands?
Here is the list :

- **blockdimension [dimension_name]** : Block the dimension provided.
- **unblockdimension [dimension_name]** : Unblock the dimension provided.
- **/languagedimension [language]** : Change the language used by the mod for the player.

# What is the translations system?
A config file _"dimensionblocker_translations.json"_ is automatically generated on server start, you can add translations as below that your players can use with the **/languagedimension** command (here i added a french translation):

```json
{
  "en": {
    "dimension_cancel": "You can\u0027t teleport to this dimension.",
    "blockdimension_success": "Dimension is now blocked!",
    "blockdimension_failure": "Dimension is already blocked.",
    "unblockdimension_success": "Dimension is no longer blocked!",
    "unblockdimension_failure": "Dimension is not blocked.",
    "dimensionlanguage_success": "DIMENSION language changed!",
    "dimensionlanguage_failure": "Error : The language provided is invalid.",
    "version": "1"
  },
"fr": {
    "dimension_cancel": "Vous ne pouvez pas vous téléporter dans cette dimension.",
    "blockdimension_success": "La dimension est maintenant bloquée !",
    "blockdimension_failure": "La dimension est déjà bloquée.",
    "unblockdimension_success": "La dimension n'est plus bloquée !",
    "unblockdimension_failure": "La dimension n'est pas bloquée.",
    "dimensionlanguage_success": "La langue de la dimension a été modifiée !",
    "dimensionlanguage_failure": "Erreur : La langue fournie est invalide.",
    "version": "1"
  }
}
```