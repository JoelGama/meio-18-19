import numpy as np
import matplotlib.pyplot as plt

profit = np.genfromtxt("grid3_profit.csv", delimiter=",", skip_header=True)
service = np.genfromtxt("grid3_service.csv", delimiter=",", skip_header=True)

plt.imshow(profit[:,1:], cmap='hot', interpolation='nearest', origin='lower', extent=[0, 15000, 0, 10000])
plt.show()

plt.imshow(service[:,1:], cmap='hot', interpolation='nearest', origin='lower', extent=[0, 15000, 0, 10000])
plt.show()
