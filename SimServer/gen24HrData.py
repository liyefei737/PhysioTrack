import sys
from datetime import datetime
from datetime import timedelta
from random import randint
from random import uniform
import zipfile

bodyPosArr = ['UPRIGHT', 'SUPINE', 'PRONE', 'SIDE']
motionArr = ['STOPPED', 'MOVINGFAST', 'MOVINGSLOWLY']
lastAccX = 0
lastAccY = 0
lastAccZ = 0
lastRR = 0
lastHR = 0
lastST = 0.0
lastCT = 0.0
invalidCT = False
invalidST = False
invalidRR = False
invalidHR = False


def getBodyPosition():
	return ' ' + bodyPosArr[randint(0,3)]

def getMotion():
	return ' ' + motionArr[randint(0,2)]

def getSign():
	if randint(0,1) == 0:
		return -1
	else:
		return 1

def getAcceleration():
	global lastAccX
	global lastAccY
	global lastAccZ
	newAccX = 0
	newAccY = 0
	newAccZ =0

	if lastAccZ == 0 and lastAccY == 0 and lastAccX == 0:
		newAccX = randint(-300, 300)
		newAccY = randint(-300, 300)
		newAccZ = randint(-300, 300)
	else:

		newAccX = max(min(lastAccX + getSign() * (randint(0, 5)), 300), -300)
		newAccY = max(min(lastAccY + getSign() * (randint(0, 5)), 300), -300)
		newAccZ = max(min(lastAccZ + getSign() * (randint(0, 5)), 300), -300)

	lastAccX = newAccX
	lastAccY = newAccY
	lastAccZ = newAccZ

	return str(newAccX), str(newAccY), str(newAccZ)

def getHeartRate():
	global invalidHR
	global lastHR
	newHR = 0
	if invalidHR:
		return str(-1)
	if lastHR == 0:
		newHR = randint(60, 80)
	else:
		newHR = max(min(lastHR + getSign()*randint(0,4), 200), 25)

	lastHR = newHR
	return str(newHR)

def getRespirationRate():
	global invalidRR
	global lastRR
	newRR = 0
	if invalidRR:
		return str(-1)

	if lastRR == 0:
		newRR = randint(12,18)
	else:
		newRR = max(min(lastRR + getSign()*randint(0,4), 70), 2)

	lastRR = newRR
	return str(newRR)


def getSkinTemp():
	global invalidST
	global lastST
	newST = 0
	if invalidST:
		return str(-1)
	if lastST == 0.0:
		newST = uniform(32.1, 36.0)
	else:
		newST = max(min(lastST + getSign()*uniform(0, 0.1), 40), 10)

	lastST = newST
	

	return "{:.1f}".format(newST) 


def getCoreTemp():
	global invalidCT
	global lastCT
	newCT = 0
	if invalidCT:
		return str(-1)
	if lastCT == 0.0:
		newCT = uniform(36.1, 37.8)
	else:
		newCT = max(min(lastCT + getSign()*uniform(0, 0.01), 41), 30)

	lastCT = newCT
	return "{:.1f}".format(newCT)


def main():
	if (len(sys.argv) < 3):
		print('Usage as follows: python gen24HrData.py outfilename -[options]')
		print "-a 	populate skin temp, core temp, rr and hr with valid values"
		print "-nct		populate core temp with -1"
		print "-nst		populate skin temp with -1"
		print "-nrr		populate rr with -1"
		print "-nhr		populate hr with -1"
		sys.exit(2)

	fileName = sys.argv[1]
	options = sys.argv[2:]
	global invalidCT
	global invalidST
	global invalidRR
	global invalidHR

	if '-nct' in options:
		invalidCT = True
	if '-nst' in options:
		invalidST = True
	if 'nrr' in options:
		invalidRR = True
	if 'nhr' in options:
		invalidHR = True

	date = datetime(2017, 02, 25, 0, 0, 0, 0)
	dateFormatString = '%m/%d/%Y %H:%M:%S.%f'

	j = 0
	fileNameIter = fileName + '_' + str(j) +'.csv'

	f = open(fileNameIter, 'w')
	f.write('DateTime,Core_Temp,Skin_Temp,AccX,AccY,AccZ,BodyPosition,Motion,Belt Breathing Rate,ECG Heart Rate\n')

	for i in range(0, 2160000):  #milliseconds in a day
		acc = getAcceleration()
		if i%1500 == 0:
			line = date.strftime(dateFormatString)[:-3] + ',' + getCoreTemp() + ',' + getSkinTemp() + ',' + acc[0] + ',' + acc[1] + ',' + acc[2] + ',' + getBodyPosition() + ',' + getMotion() + ',' + getRespirationRate() +','+getHeartRate()
		else:
			line = date.strftime(dateFormatString)[:-3] + ',' + "{:.1f}".format(lastCT) + ',' + "{:.1f}".format(lastST) + ',' + acc[0] + ',' + acc[1] + ',' + acc[2] + ',' + getBodyPosition() + ',' + getMotion() + ',' + str(lastRR) +','+str(lastHR)
		f.write(line + '\n')
		date = date + timedelta(milliseconds = 40)
		
		if (i != 0 and i%200000 == 0):
			j+=1
			f.close()
			with zipfile.ZipFile(fileNameIter + '.zip', 'w', zipfile.ZIP_DEFLATED) as myzip:
				myzip.write(fileNameIter)

			fileNameIter = fileName+"_"+str(j)+".csv"
			f = open(fileNameIter, 'w')


	with zipfile.ZipFile(fileNameIter + '.zip', 'w', zipfile.ZIP_DEFLATED) as myzip:
		myzip.write(fileNameIter)
	f.close()

		

if __name__ == "__main__":
		main()