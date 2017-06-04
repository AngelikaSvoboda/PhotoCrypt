import sys
from random import random
from PIL import Image, ImageDraw
import os
#import Image, ImageDraw

import datetime
import subprocess

#import struct
import socket 
from threading import Thread 
from socketserver import ThreadingMixIn

class GenerateLatex(Thread):
	
	# Liest das Bild ein unter angegebenen Dateipfad file und gibt ein
	# Dictionary mit Position und Pixelfarbe zurück
	@classmethod
	def readPicture(self, file):		image = Image.open(file)
		D = {}
		x,y = image.size
		
		for i in range(0,x):
			for j in range(0,y):
				pix = image.getpixel((i,j))
				#print(pix)
				b = pix[0]
				D[(i,j)] = b
		return (D, x, y)
		
	@classmethod	
	def receiveImage(self, path, client):
		#Empfange Bild
		print("Empfange Bild")

		with open(path, 'wb') as img:
			while True:
				data = client.recv(1024)
				if not data:
					break
				img.write(data)
		client.close()
	
	@classmethod
	def generateLaTeXFile(self, path):
		#print(path)
		proc = subprocess.Popen(['xelatex', path])
		proc.communicate()

	@classmethod
	def drawPixel(self, draw, x, y, b):
		if (b==False):
			draw.point((x, y), fill=1)
			draw.point((x+1, y+1), fill=1)

		else:
			draw.point((x, y+1), fill=1)
			draw.point((x+1, y), fill=1)
			
		return

	@classmethod
	def generatePNGFiles(self, D, x, y, path):
		width=1
		L = []
		for(i,j), b in D.items():
			L.append((i,j,b))
		L = list(sorted(L))
		bild_1 = Image.new("1", (2 * x, 2 * y))
		bild_2 = Image.new("1", (2 * x, 2 * y))
		draw_1 = ImageDraw.Draw(bild_1)
		draw_2 = ImageDraw.Draw(bild_2)
		
		nr0 = 0
		nr1 = 0

		for (x,y,b) in L:
			r = random()
			if b==False:
				if r<=0.5:
					self.drawPixel(draw_1, 2*x, 2*y, False)
					self.drawPixel(draw_2, 2*x, 2*y, True)
				else:
					self.drawPixel(draw_1, 2*x, 2*y, True)
					self.drawPixel(draw_2, 2*x, 2*y, False)
			else:
				if r<=0.5:
					self.drawPixel(draw_1, 2*x, 2*y, False)
					self.drawPixel(draw_2, 2*x, 2*y, False)
				else:
					self.drawPixel(draw_1, 2*x, 2*y, True)
					self.drawPixel(draw_2, 2*x, 2*y, True)

		del draw_1
		del draw_2
		bild_1.save(os.path.normpath(os.getcwd() + "/" + path + "/bild-1.png"), "PNG")
		bild_2.save(os.path.normpath(os.getcwd() + "/" + path + "/bild-2.png"), "PNG")
	
	#nur unter Linux
	def printLaTeX(self, file):
		byteFile = open(file, "rb")
		data = byteFile.read()
		lpr =  subprocess.Popen("/usr/bin/lpr", stdin=subprocess.PIPE)
		lpr.stdin.write(data)
	
	def __init__(self,client,ip,port):
		Thread.__init__(self) 
		self.client = client
		self.ip = ip 
		self.port = port 
		print ("[+] New server socket thread started for " + ip + ":" + str(port) )
		
	def run(self):
		dt = datetime.datetime.now()
		file = dt.strftime('%d%m%Y%H%M%S') + ".png"
		folder = "img/"
		path = folder + file
		self.receiveImage(path, client)

		D, x, y = self.readPicture(path)
		self.generatePNGFiles(D, x, y, "latex")
		self.generateLaTeXFile(os.path.normpath(os.getcwd() + "/latex/" + "genslides.tex"))
		#self.printLaTeX("genslides.pdf")
		return
		
#if __name__ == "__main__":
#	main(sys.argv)

TCP_IP = "192.168.2.108"
TCP_PORT = 10000
tcpServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM) 
tcpServer.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) 
tcpServer.bind((TCP_IP, TCP_PORT)) 
threads = []

while True: 
	print ("Multithreaded Python server : Waiting for connections from TCP clients..." )
	tcpServer.listen(4)
	client, (ip,port) = tcpServer.accept() 
	#client =""
	#ip=""
	#port=1
	newthread = GenerateLatex(client,ip,port)
	newthread.start() 
	threads.append(newthread)
	#break
	
for t in threads: 
	t.join() 
