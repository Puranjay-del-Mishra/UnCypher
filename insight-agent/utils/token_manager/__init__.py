from .token_manager import TokenManager

try:
    token_manager = TokenManager()
except Exception as e:
    print(f"⛔ Failed to initialize TokenManager!: {e}")
    token_manager = None