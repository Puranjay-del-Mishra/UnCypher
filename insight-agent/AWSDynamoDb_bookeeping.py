import boto3
from boto3.dynamodb.conditions import Key
from decimal import Decimal

# Set your table name
TABLE_NAME = "user"

# Initialize DynamoDB client and resource
dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table(TABLE_NAME)

# Scan the table (read all items)
def scan_table():
    response = table.scan()
    data = response['Items']

    # Keep scanning if there are more items
    while 'LastEvaluatedKey' in response:
        response = table.scan(ExclusiveStartKey=response['LastEvaluatedKey'])
        data.extend(response['Items'])

    return data

# Batch delete
def batch_delete(items):
    # Extract key attribute names from schema
    key_names = [key['AttributeName'] for key in table.key_schema]

    with table.batch_writer() as batch:
        for item in items:
            key = {k: item[k] for k in key_names}
            batch.delete_item(Key=key)


# Main
if __name__ == "__main__":
    print(f"Scanning table {TABLE_NAME}...")
    items = scan_table()
    print(f"Found {len(items)} items. Deleting...")
    batch_delete(items)
    print("All items deleted.")
