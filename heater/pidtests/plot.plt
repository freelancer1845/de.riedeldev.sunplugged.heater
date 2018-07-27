
set terminal wxt size 1400,1000

file = "piddata16.txt"

set multiplot


plot file using 1:2 lc rgb 'green', file using 1:3 lc rgb 'red'


set yrange [-0.2:1.2]
plot file using 1:4 lc rgb 'gold'

