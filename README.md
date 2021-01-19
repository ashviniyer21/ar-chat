# ar-chat
Allows for video call in AR

## Inspiration
During the pandemic, we are finding ourselves desperate for more life-like human interaction as we are ordered to stay home and away from social gatherings. Our group was frustrated with the limitations of 2D video conferencing applications, which ironically made us feel more disconnected by only seeing each other sitting in small little boxes on our screen. In addition, the lack of other niche features required while learning made it difficult for our teachers to teach effectively. We decided that with the resources at hand, we would be able to make a unique video conferencing application with the use of augmented reality to give a more realistic conversation experience.

## What it does
AR Chat is a video conferencing platform that creates a face-to-face meeting experience with another person. Our main goal for this app was to create a life-like meeting experience, such that people can feel more connected when meeting online. Since we integrated the Echo AR cloud, we are able to take advantage of the cloud features by adding in additional elements to the meeting experience. We came up with three example use cases, and made a proof of concept for each one: 

-Education: Right now, education online is fairly limited, and suffers from not having the experience of being with a teacher in person. Meeting with a teacher or student in AR makes it feel more like a classroom environment, and you can also take advantage of some cool AR features, such as a 3D graphing visualizer we implemented that can graph polynomials (stored as metadata in the Echo AR cloud).

-Collaboration: Being able to collaborate with someone often involves hands-on activities. While this cannot be achieved fully in AR, you can at least share things like 3D CAD models or designs that people can view in the AR world, and they can by synced using the Echo AR cloud such that both people can be viewing the same design at the same time while also seeing each other. For this proof of concept, we took an example cad model from online (//https://free3d.com/3d-model/industry-robot-arm-9499.html) and displayed it in AR.

-Recreational: It's always good to spend time and play games with friends, and now playing games online can be a more life-like experience in AR. You can see the person you are playing with as well as both have the game board in front of you, as if you were both sitting around a table. For this proof of concept, we uploaded a chess board model to demonstrate how a game would look.

## How we built it
We made a webapp using node.js, and stream images between devices using a socket. Devices are connected using a room code, and when two devices enter the same code they are connected to each other. We used Tensorflow.js and the BodyPix person segmentation model to extract people from the webcam, and used AR.js to display it in AR on certain AR markers. (https://blog.tensorflow.org/2019/11/updated-bodypix-2.html)

### Images from the webcam follow a pipeline to get from the webcam to AR:
1. Image is taken from webcam
2. Using TensorFlow.js BodyPix image segmentation model, we segmented the image and detected where the person was. 
3. Given the detected person, we cut out all pixels that are not the person (background) and were left with a transparent image of just the person.
4. We send that image to the other device using the socket.
5. The image is wrapped around a cylinder 3D object using Aframe.js. The cylinder is used to give the image more depth, so it is a slightly curved image rather than a flat image. This in no way replicates the depth of a human model, but the extra depth of the cylinder makes it look a little bit nicer.
6. The image is then displayed in AR using AR.js

### How we made the proof of concepts using Echo AR:
We used the Echo AR API to receive models for the proof of concepts in realtime. We also used the API to store metadata for the graphing proof of concept as well as download the 3D models from the cloud. The Echo AR cloud contained all of the 3D models used in the AR demo except for the AR webcam, which are locally added to the AR.js scene as the images are received from the socket.

In the future, Echo AR can be used to implement the real version of many of the proof of concepts we showed, such as using it for storing the position of the chess pieces or for allowing users to upload 3D CAD models and have them show up in the scene in realtime for easy collaboration and review of designs.

## Challenges we ran into
Our main challenge was that we found out that iPhones (and most mobile devices) are not capable of using the front and back facing camera at the same time. This basically ruined our entire plan of having an AR video chat program. After some research, we found that newer phones (such as the iPhone 11 plus and above) are starting to support this dual webcam feature, but only when using Apple's SDK in a native swift app. Therefore, we decided that our project idea was not lost, and we would just need to improvise a proof of concept for this hackathon to use a one-way webcam stream rather than two-way since we were writing a webapp that could not take advantage of the Apple dual webcam support. If we were bringing this into production, we would be able to use the dual webcam support by writing a swift app instead of a webapp, but we didn't have the time to do that in this hackathon.

Another challenge we faced was latency. We noticed quite a bit of latency, and you will probably notice that in the video as well. This is solvable by optimizing all the data we are sending, but once again we didn't have time for that so we had to deal with the latency for the hackathon.

## Accomplishments that we're proud of
We created a full system that allows a device to stream a webcam to another device and display it in AR, as well as utilized a machine learning model to segment the image and cutout the background. We provided good proof of concepts for this application, and although we could not get a full product working due to the limitations described above, we feel this is a good proof of concept to show the idea works, and the functionality ended up working quite well.

## What we learned
We are all relatively new to webapp creation, so this was a good learning experience for making webapps. None of us have used AR before, so this was a good learning experience to introduce us into how to use AR and what it can do, especially given the easy to implement Echo AR Cloud and AR.js. We are also all relatively new to machine learning, so it was fun learning about how to implement a machine learning model and how it works to segment an image and isolate a person or object.

## What's next for ar-chat
We would like to build more modules geared towards education. We hope that students can be positioned in any way the organizer feels is necessary, whether it be groups, columns, or just a standard grid. We would also build more importable tools such as physics simulations, data visualization, and better idea organization. In addition, as mentioned previously, we would like to implement the logic for the movement of chess pieces, though it is relatively simple to do. Adding more games as well to make this a fully fledged platform for multiplayer games would be another step forward in making AR Chat a versatile video chatting platform.
