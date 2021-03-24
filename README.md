# Tawk_Android_Demo

# Clone the Project:
URL: https://github.com/Bipinsakariya/Tawk_Android_Demo.git

# Run Anroid Project: 
Link: https://developer.android.com/training/basics/firstapp/running-app
 
# MVC structure:
Activity, Fragment
Activities are containers for Fragments. Fragments handle lifecycle of views. After Views creation, they are subscribed to listen for ScreenState changes. Likewise, before Views destruction, they are unsubscribed to prevent memory leaks.

Model
Every model is stored in singleton called Storage. It provides basic methods to get item with id, write it, or update. There are two types of items:

ScreenState - which represents current state of a screen (information like: is progress bar visible?; is animation running?; what's scroll position?) and holds list of ItemModels ids.
ItemModel - contains data used for filling one view. When model changes, it notifies corresponding ScreenStates to update themselves.

View
Layout - Group of Views. updates itselve with data from ScreenState. Passes ItemModels ids to its children.
View - after update trigger, it gets ItemModel from Storage, and displays the data.

Controller
Each controller contains list of static methods used for changing models inside Storage

# Folder structure:
- Activity : Each screen components should be stored inside Activity's own folder.
- Fragment : Each screen components should be stored inside Fragment's own folder.
- Utils:
    - Glob: Folder that contains basic value names like Common String value, Common Function etc.
    - Animation: All type of animation stored inside animation.
- Model: Convert GSON to JSON data.
- Service: ALL type of API releated file should be stored.
