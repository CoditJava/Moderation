# Moderation
I created this project so younger developers can up their game. Even though the code is rather simple, there are howerver some special things in this bukkit moderation project. 

This project contains some pretty important features for a moderation plugin:
 - Moderation interface
 - Freeze player (With inventory that cannot be bypassed with F6 (Twitch bypass))
 - Report system with an inventory to store the active reports
 - ChatManager (Muted, slowed, restricted)
 

If you have any questions regarding some of the codes you can reach out to me on discord Vic#4111.

Here are the classes that aren't mine in this project:
 - InventoryUI
 - UIListener
 - CC (Chat color API)
 - ItemUtil
 
 I used Maven with this project but it's very easy to adapt it and be able to export it without. Just take a 1.8.8 spigot with lombok (Getter, setter etc) in dependencies, remove the pom.xml, and you should be good to go!
