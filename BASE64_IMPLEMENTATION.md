# Base64 Feature Implementation

## Overview

This implementation ports the base64 link decoding feature from the ChatTriggers version to the Fabric mod. The feature automatically detects and decodes base64-encoded links in Guild chat messages.

## Implementation Details

### Components

1. **Base64Decoder.java** - Utility class for base64 decoding
   - Direct port of the `dc()` function from the original ChatTriggers implementation
   - Manual implementation using character mapping
   - Includes validation methods

2. **Base64ChatHandler.java** - Chat event handler
   - Registers with Fabric's ClientReceiveMessageEvents.ALLOW_GAME
   - Uses regex patterns to match Guild messages containing base64 content
   - Cancels original messages and sends formatted replacements

3. **ModConfig.java** - Configuration system
   - Manages enable/disable state
   - Configurable bot name and separator
   - Placeholder for future file-based configuration

4. **StarcrestaddonsClient.java** - Main client initializer
   - Registers the base64 chat handler
   - Loads configuration on startup

### Message Pattern

The handler looks for messages matching this pattern:
```
§r§2Guild > [VIP] MAChatbridge [EQUITE]: §rPlayerName » [Image] base64content§r
```

When matched, it:
1. Extracts the bot name, player name, and base64 content
2. Validates the bot name matches the configured value
3. Decodes the base64 content
4. Cancels the original message
5. Sends a formatted replacement with the decoded link

### Test Results

The implementation has been tested with a standalone Java test that confirms:
- Base64 decoding works correctly
- Pattern matching identifies the correct message format
- Message processing logic functions as expected
- Full workflow from detection to replacement works

Example test output:
```
Test 1 - Basic decoding:
  Original: https://i.imgur.com/abc123.jpg
  Encoded:  aHR0cHM6Ly9pLmltZ3VyLmNvbS9hYmMxMjMuanBn
  Decoded:  https://i.imgur.com/abc123.jpg
  Match:    true

Test 2 - Pattern matching:
  Message: §r§2Guild > [VIP] MAChatbridge [EQUITE]: §rTestPlayer » [Image] aHR0cHM6Ly9pLmltZ3VyLmNvbS9hYmMxMjMuanBn§r
  First pattern matched: true
  Extracted bot:       MAChatbridge
  Extracted player:    TestPlayer
  Extracted separator: ' »'
  Extracted base64:    aHR0cHM6Ly9pLmltZ3VyLmNvbS9hYmMxMjMuanBn
  Decoded link:        https://i.imgur.com/abc123.jpg

Test 3 - Full processing simulation:
  Original message would be cancelled
  Replacement message: Guild > [VIP] MAChatbridge [EQUITE] TestPlayer » https://i.imgur.com/abc123.jpg
  Processing successful: true
```

## Configuration

The feature can be configured through the ModConfig class:

- `base64Enabled` - Enable/disable the feature (default: true)
- `botName` - Name of the bot to monitor (default: "MAChatbridge")
- `separator` - Separator used in messages (default: " »")

## Usage

Once the mod is built and installed:

1. Join a server with Guild chat
2. When a bot posts a message with base64 encoded content in the format `[Image] <base64>`
3. The mod will automatically decode and display the link
4. The original encoded message will be hidden

## Build Notes

The current build configuration may need adjustments for the specific environment. The implementation uses:
- Minecraft 1.21.1
- Fabric Loader 0.15.11
- Fabric API 0.100.8+1.21.1
- fabric-loom gradle plugin

If build issues persist, the core implementation can be copied to a working Fabric mod project with compatible versions.