# RSS Reader
A barebones RSS reader for Android, written as my final assignment for Algonquin College's "Mobile Graphical Interface Programming" course (CST2335)

<img width="4380" height="1920" alt="A collage of screenshots showcasing different UI views: feed, article, favourites, and an error screen." src="https://github.com/user-attachments/assets/b14b0a44-8732-469c-bda8-6e868685a73b" />

For this assignment we were to pick from three topics: 
* A viewer for NASA's image of the day
* A reader of The Guardian headlines, utilising their open platform API
* A BBC News headline reader, utilising their North American RSS feed

We then had to write an Android application, using Java, that implemented our chosen topic as well as integrated concepts learned in the course, including older technologies such as AsyncTask, ListView, and SharedPreferences.

I also managed to implement a few bonus features before the assignment's deadline:
* Support for setting a custom RSS feed
* Jetpack libraries
  * Navigation (fragment management)
  * Room (cleaner data architecture, since I'm not calling SQLite APIs directly)
  * ViewBinding (null safety, type safety, and a nicer syntax over findViewById)
* Caching of the feed in-memory via a ViewModel
* Pull-to-refresh (SwipeRefreshLayout)
* Informative error pages, allowing for recovery without restarting the application
