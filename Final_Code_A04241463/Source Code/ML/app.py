import cv2
import numpy as np
import matplotlib.pyplot as plt
from keras.models import load_model
from keras.preprocessing.image import img_to_array
from flask import Flask, request, render_template, flash, redirect, url_for, jsonify
from firebase import firebase  
firebase = firebase.FirebaseApplication('https://cloth-recommendation-default-rtdb.firebaseio.com/', None)  

loaded_model = load_model('model.h5')

classes = ['Jackets', 'Pants', 'Tops', 'Track Pants', 'Tshirts']

import os,requests
def download(url):
	# print(url)
	# url = url.replace('slash','/').replace('AND','&').replace('PER','%')
	# print(url)

	get_response = requests.get(firebase.get('cloths/-N0UaU40OeYQevwNwfNO/img',''),stream=True)
	file_name = 'test.jpeg'
	with open(file_name, 'wb') as f:
		for chunk in get_response.iter_content(chunk_size=1024):
			if chunk: # filter out keep-alive new chunks
				f.write(chunk)


# download("https:slashslashfirebasestorage.googleapis.comslashv0slashbslashcloth-recommendation.appspot.comslashoslashimages%2Fb924fa4f-2c56-4b15-9fa8-d5bee3d791c7?alt=media&token=4c7658db-0dee-4210-b30e-8d5ee599a99c")

from skimage import io



def predict_img(file_path):
  image = io.imread(firebase.get('cloths/-N0UaU40OeYQevwNwfNO/img',''))
#   image = cv2.imread(file_path)
  image = cv2.resize(image, ((64,64)))  
  # plt.imshow(image)
  # plt.show()

  image = img_to_array(image) 
  np_image_list = np.array([image], dtype=np.float16) / 225.0


  preds = loaded_model.predict(np_image_list,batch_size=None, verbose=0)
  labels = np.argmax(preds, axis=-1)
  print(labels)    
  return classes[labels[0]]


download('')
print(predict_img('test.jpeg'))

app = Flask(__name__)

global filename
filename = ""

app.secret_key = 'super secret key'
app.config['SESSION_TYPE'] = 'filesystem'


@app.route('/predict',methods = ['POST','GET'])
def predict():

	try:
		url = request.args.get("url")
		download(url)
		result = predict_img('test.jpeg')
	except Exception as e:
		print(e)
	return jsonify({'result':result,'error':'false'})
	



if __name__ == "__main__":
    app.run(debug=True)





result = firebase.get('cloths/-N0UaU40OeYQevwNwfNO/img','')
print(result)
