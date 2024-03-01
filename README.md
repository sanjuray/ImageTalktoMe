This app is built to read text from image captured using MLkit dependency.
This app uses internet permissions and camera permissions. 
For using camera, we use ContextCompat to check for camera service,
ActivityCompat for requesting camera service and MediaStore.ACTION_IMAGE_CAPTURE
for using camera and getting the image. Utillizing onActivityResult we retrieve th
image into a bitmap used for detecting text. 
For detecting we use TextRecognizer to recognize text and it's process(image) is 
is used for processing/ recognizing the text. Then we set the detected text onto 
the textView on the screen.

<video src="https://github.com/sanjuray/ImageTalktoMe/assets/94555333/47dfe016-352e-4407-a67b-c57604aac040" height=550 weight=450/>

