// Basic ChatMessage structure (documented for dev reference)
export function createChatMessage({
    id,
            userId,
            sender,
            text,
            timestamp,
            loading = false,
}) {
        return {
id,
userId,
sender, // "user" | "assistant"
text,
timestamp,
loading,
        };
        }

// Mapper from backend DTO to frontend model
export function mapDtoToChatMessage(dto) {
    return {
            id: dto.messageId,
            userId: dto.userId,
            sender: dto.role,
            text: dto.content,
            timestamp: dto.timestamp,
  };
}