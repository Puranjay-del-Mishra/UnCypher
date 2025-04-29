import os
import time
import threading
import requests

class TokenManager:
    def __init__(self, refresh_interval_minutes=13):
        self.token = None
        self.expiry = 0  # UNIX timestamp
        self.refresh_interval = refresh_interval_minutes * 60  # seconds

        # Start background refresh loop
        self.start_background_refresh()

    def fetch_new_token(self):
        try:
            print("🔄 [TokenManager] Fetching fresh agent token...")
            response = requests.get("http://localhost:8080/admin/generate-agent-token")
            response.raise_for_status()
            data = response.json()
            token = data["accessToken"]
            self.token = token
            self.expiry = time.time() + (14 * 60)  # safe buffer: 14 minutes lifetime
            os.environ["AGENT_TOKEN"] = token
            print("✅ [TokenManager] New agent token fetched and set.")
        except Exception as e:
            print(f"⛔ [TokenManager] Failed to fetch agent token: {e}")

    def get_token(self):
        now = time.time()
        if not self.token or now >= self.expiry:
            self.fetch_new_token()
        return self.token

    def start_background_refresh(self):
        def refresh_loop():
            while True:
                time.sleep(self.refresh_interval)
                self.fetch_new_token()

        threading.Thread(target=refresh_loop, daemon=True).start()
