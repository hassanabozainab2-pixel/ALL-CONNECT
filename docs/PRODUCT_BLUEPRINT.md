# ALL CONNECT — Product Blueprint

## Product goal
A secure Arabic-first Android hub for a user's social communications: a unified inbox for notification previews, supported account connections, and permitted actions. It does not bypass the rules or private APIs of social networks.

## Core user experience
- Arabic RTL interface, accessible typography, light/dark themes.
- Sign up/sign in with Supabase Auth (phone OTP or email/password); never store plaintext passwords.
- Home dashboard with connected services, unread counts, and recent activity.
- Unified inbox with source, sender, preview, timestamp, search, filters, saved items, and user-controlled deletion.
- Per-service connection status and clear explanation of supported capabilities.
- User settings: privacy, retention, export/delete account, notification preferences.
- Subscription screen with Free and paid plans; subscription status and entitlements enforced server-side.
- Owner/admin console separated from ordinary user access.

## Integration policy and feasibility
- Android Notification Listener can show only notification content Android actually exposes after the user explicitly grants Notification Access. It cannot guarantee every message or historical conversation.
- Replies/actions are enabled only where Android notification actions or an official provider API permits them.
- Official OAuth/API integrations are evaluated per provider. Provider permissions, review requirements, rate limits, and commercial terms apply.
- Never request social-network passwords, scrape private endpoints, automate account access deceptively, or bypass MFA/platform protections.
- Initial provider assessment: WhatsApp/Meta products, Telegram, Instagram, Facebook/Messenger, TikTok, and X must each be evaluated independently. A provider may be display-only or unavailable for personal accounts.

## Backend design (new dedicated ALL CONNECT Supabase project)
- Supabase Auth for identities and sessions.
- profiles: user profile and safe display preferences.
- connected_accounts: provider, provider subject, encrypted token reference, scopes, status, expiry metadata. Secrets are never exposed to client reads.
- notification_events: user-owned notification previews, source, sender label, body preview, timestamps, retention metadata.
- plans, subscriptions, subscription_events: plan entitlements and auditable subscription state.
- admin_roles: server-managed admin roles; never trust user-editable metadata for authorization.
- RLS on every exposed table; policies scope all user data to auth.uid(). Service secrets remain server-side.
- Payment integration is deferred until the product and Egyptian payment provider are selected; verify payment webhooks server-side before granting entitlements.

## Delivery phases
1. Product foundation: dedicated backend, auth, profiles, RLS, and account lifecycle.
2. Android experience: polished dashboard, notification permission education, local inbox, search, filters, and deletion.
3. Provider integrations: official API capability matrix and incremental OAuth connections.
4. Subscription system: plans, server-verified payment events, entitlements, and owner dashboard.
5. Security, privacy, accessibility, device testing, Play policy review, and release signing.

## Non-goals / safety boundaries
- No claim of universal control over every social platform.
- No reading of private messages beyond user-authorized notification previews or official APIs.
- No sending messages without a user action and an allowed platform capability.
- No production credentials, signing keys, or payment secrets committed to Git.