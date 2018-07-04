## Witherbar
Witherbar is an amazing useful updated stable liberary to create and edit bossbars on your server that does not bring any lag or issues to your server and the compiled version can be downloaded from the official SpigotMC resource page of Witherbar.
> **Tip:**  Use the new built-in Spigot methods for bossbars if you are using the latest version of Spigot.
</br>
 
## Developers
Here is an example with built-in methods for developers that want to use the Witherbar liberary to create and edit bossbars.
```ruby
# Create the Witherbar object before we can edit it.
Witherbar example = new Witherbar("Example Title");

# Add a player to let the player see the bossbar.
example.addPlayer(player);

# Remove a player to let him not see the bossbar anymore.
example.removePlayer(player);

# Set the title for the bossbar (works with color-codes).
example.setTitle("New Title");

# Set the progess for the bossbar (with a float value).
example.setProgess(0.5);

# Returns a boolean if a player can see the bossbar.
example.hasPlayer(player);
```
</br>
  
## Credits
**Marido** for updating and resolving issues in the Witherbar class.
**Fr33stylerR0** for adding the basic stuff to the Witherbar class.