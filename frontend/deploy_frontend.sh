PORT=3000
echo "Checking for process running on port $PORT..."
PID=$(sudo lsof -ti:$PORT)  # Get the PID of the process using the port

if [ -n "$PID" ]; then
    echo "Killing process $PID running on port $PORT..."
    sudo kill -9 $PID
    echo "Process killed successfully!"
else
    echo "No process found running on port $PORT."
fi
npm run dev