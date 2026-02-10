# Android App UI 优化方案文档（品牌增强版）

## 项目概述
**项目名称**: 用户认证系统 - Android端  
**技术栈**: Kotlin + Jetpack Compose + Material3  
**优化目标**: 在km.md自然清新风格基础上，融合mx.md品牌装饰元素，打造具有品牌辨识度和视觉记忆点的UI系统

**设计理念**: 「自然清新 + 品牌装饰」的融合风格
- 🌿 **自然清新**: 保留清新绿色系，呼应水果营养查询功能
- ✨ **品牌装饰**: 添加装饰性元素（渐变条、角落装饰、倾斜元素）增强品牌辨识度
- 🎨 **视觉层次**: 通过装饰元素建立更清晰的信息层级

---

## 一、当前UI问题诊断

### 1.1 色彩系统问题
| 问题 | 现状 | 影响 |
|------|------|------|
| 主题色缺乏品牌辨识度 | 使用默认紫色系 (Purple40/80) | 看起来像模板Demo，无特色 |
| 色彩层次单一 | 仅使用primary/secondary/tertiary | 缺乏视觉深度和层次感 |
| 语义化颜色缺失 | 没有自定义语义色彩 | 不同场景（成功/警告/信息）表现不够 |
| 暗色模式适配不完整 | 仅有基础darkColorScheme | 暗色模式下的细节体验差 |

### 1.2 排版系统问题
| 问题 | 现状 | 影响 |
|------|------|------|
| 字体使用默认系统字体 | FontFamily.Default | 缺乏个性和精致感 |
| 字号层次不够明确 | 仅有bodyLarge定义 | 标题、副标题等样式不统一 |
| 行高和字距未优化 | 使用Material默认值 | 阅读体验不佳 |
| 中文排版未优化 | 英文习惯的字间距 | 中文显示过于紧凑 |

### 1.3 视觉装饰缺失
| 问题 | 现状 | 影响 |
|------|------|------|
| 卡片缺少装饰元素 | 纯色背景，无视觉亮点 | 信息层次不清晰，品牌感弱 |
| 背景过于单调 | 纯色或简单渐变 | 缺乏品牌记忆点 |
| 无品牌标识元素 | 缺少水印、装饰图形 | 用户难以建立品牌认知 |
| 动效过于简单 | 基础CircularProgressIndicator | 缺乏精致感 |

### 1.4 交互体验问题
| 问题 | 现状 | 影响 |
|------|------|------|
| 缺少过渡动画 | 页面切换突兀 | 用户体验生硬 |
| 按钮缺少交互反馈 | 仅有默认点击效果 | 缺乏高级感的交互反馈 |
| 涟漪效果未定制 | 使用默认涟漪 | 与品牌色彩不协调 |
| 加载状态单一 | 仅有基础加载动画 | 等待体验枯燥 |

---

## 二、品牌设计系统

### 2.1 色彩方案（自然绿 + 暖橙点缀）

#### 核心配色（保留km.md方案）
```kotlin
// Color.kt - 完整色彩系统
object AppColors {
    // 🌟 主色系 (Primary) - 清新绿色系（健康、自然）
    val Primary = Color(0xFF4CAF50)           // 明亮绿
    val PrimaryLight = Color(0xFF81C784)      // 浅绿 - hover态
    val PrimaryDark = Color(0xFF2E7D32)       // 深绿 - 按压态/禁用态
    val OnPrimary = Color.White               // 白色文字
    
    // 辅助色系 (Secondary) - 暖橙色（活力、温暖）
    val Secondary = Color(0xFFFF9800)         // 暖橙
    val SecondaryLight = Color(0xFFFFB74D)    // 浅橙
    val SecondaryDark = Color(0xFFF57C00)     // 深橙
    val OnSecondary = Color.White
    
    // 🌟 强调色系 - 用于品牌装饰元素
    val AccentCyan = Color(0xFF26A69A)        // 青绿色 - 科技装饰
    val AccentYellow = Color(0xFFFFEB3B)      // 新鲜黄 - 装饰点缀
    val AccentPink = Color(0xFFE91E63)        // 莓果粉 - 突出操作
    
    // 🌟 语义色系
    val Success = Color(0xFF66BB6A)           // 成功
    val SuccessLight = Color(0xFFE8F5E9)      // 浅绿背景
    val Warning = Color(0xFFFFA726)           // 警告
    val WarningLight = Color(0xFFFFF3E0)      // 浅橙背景
    val Error = Color(0xFFEF5350)             // 错误
    val ErrorLight = Color(0xFFFFEBEE)        // 浅红背景
    val Info = Color(0xFF42A5F5)              // 信息
    val InfoLight = Color(0xFFE3F2FD)         // 浅蓝背景
    
    // 🌟 中性色系 - 层次分明
    val Background = Color(0xFFF8FAF8)        // 微绿背景（暖白）
    val Surface = Color.White                  // 纯白卡片
    val SurfaceVariant = Color(0xFFF5F5F5)    // 浅灰表面
    val Outline = Color(0xFFE0E0E0)           // 边框线
    val OnBackground = Color(0xFF212121)      // 深灰文字
    val OnSurface = Color(0xFF212121)         // 深灰文字
    val OnSurfaceVariant = Color(0xFF757575)  // 中灰说明
    
    // 🌟 品牌渐变色系（新增）
    val GradientPrimary = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
    val GradientSecondary = listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
    val GradientAccent = listOf(Color(0xFF26A69A), Color(0xFF4CAF50))
    
    // 🌟 装饰色彩（新增）
    val DecorationLight = Color(0xFF4CAF50).copy(alpha = 0.08f)  // 装饰元素浅色
    val DecorationMedium = Color(0xFF4CAF50).copy(alpha = 0.15f) // 装饰元素中色
}
```

#### 品牌装饰渐变（新增）
```kotlin
// 🌟 品牌装饰渐变（融合mx.md元素）
object BrandGradients {
    // 主渐变 - 用于头部、重要卡片
    val PrimaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
    )
    
    // 次渐变 - 用于次要操作、装饰
    val SecondaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFF9800), Color(0xFFFFB74D))
    )
    
    // 背景渐变 - 用于特殊背景
    val BackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF1F8E9),  // 极浅绿
            Color.White         // 纯白
        )
    )
    
    // 🌟 登录页装饰渐变（新增）
    val LoginBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF4CAF50).copy(alpha = 0.8f),
            Color(0xFF26A69A).copy(alpha = 0.3f),
            Color(0xFFF8FAF8)
        )
    )
    
    // 🌟 径向渐变 - 用于装饰圆形（新增）
    val DecorationRadial = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.Transparent
        )
    )
}
```

#### 深色模式色彩
```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF81C784).copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFF81C784),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFB74D).copy(alpha = 0.2f),
    onSecondaryContainer = Color(0xFFFFB74D),
    tertiary = Color(0xFF4DB6AC),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D),
    error = Color(0xFFEF5350),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFB0B0B0)
)
```

### 2.2 字体系统

#### 自定义字体配置（km.md优化版）
```kotlin
// Type.kt - 完整字体层级
// 建议使用 Google Fonts 中的中文字体：
// - 标题字体: Noto Sans SC Bold - 现代、清晰
// - 正文字体: Noto Sans SC Regular - 易读性高

val NotoSansSC = FontFamily(
    Font(R.font.noto_sans_sc_regular, FontWeight.Normal),
    Font(R.font.noto_sans_sc_medium, FontWeight.Medium),
    Font(R.font.noto_sans_sc_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    // 展示文字 (Display)
    displayLarge = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    
    // 标题 (Headlines)
    headlineLarge = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    
    // 标题 (Titles)
    titleLarge = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // 正文 (Body)
    bodyLarge = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // 标签 (Labels)
    labelLarge = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = NotoSansSC,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### 2.3 间距系统

```kotlin
// Dimens.kt - 统一间距常量
object AppDimens {
    // 基础间距 (4dp grid)
    val SpacingXxs = 2.dp
    val SpacingXs = 4.dp
    val SpacingSm = 8.dp
    val SpacingMd = 16.dp
    val SpacingLg = 24.dp
    val SpacingXl = 32.dp
    val SpacingXxl = 48.dp
    val SpacingXxxl = 64.dp
    
    // 屏幕边缘间距
    val ScreenPadding = 16.dp
    val ScreenPaddingLarge = 24.dp
    
    // 卡片间距
    val CardPadding = 16.dp
    val CardPaddingSmall = 12.dp
    val CardElevation = 2.dp
    val CardElevationRaised = 4.dp
    
    // 列表间距
    val ListItemPadding = 16.dp
    val ListItemPaddingSmall = 12.dp
    val ListDividerPadding = 16.dp
    
    // 按钮间距
    val ButtonHeight = 48.dp
    val ButtonHeightSmall = 36.dp
    val ButtonPaddingHorizontal = 24.dp
    
    // 输入框间距
    val TextFieldHeight = 56.dp
    val TextFieldMinHeight = 120.dp
    
    // 🌟 圆角系统
    val CornerRadiusSmall = 4.dp
    val CornerRadiusMedium = 8.dp
    val CornerRadiusLarge = 12.dp
    val CornerRadiusXLarge = 16.dp
    val CornerRadiusFull = 24.dp
    val CornerRadiusCircular = 50.dp
    
    // 🌟 图标尺寸
    val IconSizeSmall = 16.dp
    val IconSizeMedium = 24.dp
    val IconSizeLarge = 32.dp
    val IconSizeXLarge = 48.dp
    
    // 🌟 头像尺寸
    val AvatarSizeSmall = 32.dp
    val AvatarSizeMedium = 48.dp
    val AvatarSizeLarge = 72.dp
    val AvatarSizeXLarge = 96.dp
    
    // 🌟 装饰元素尺寸（新增）
    val DecorationSizeSmall = 4.dp    // 顶部渐变条高度
    val DecorationSizeMedium = 8.dp   // 角落装饰圆点
    val DecorationSizeLarge = 80.dp   // 倾斜装饰元素
    val DecorationCircleSize = 200.dp // 装饰圆形
}
```

### 2.4 形状系统

```kotlin
// Shape.kt - 形状常量
object AppShapes {
    val Small = RoundedCornerShape(4.dp)
    val Medium = RoundedCornerShape(8.dp)
    val Large = RoundedCornerShape(12.dp)
    val XLarge = RoundedCornerShape(16.dp)
    val Full = RoundedCornerShape(24.dp)
    val Circular = RoundedCornerShape(50.dp)
    
    // 组件专用形状
    val ButtonShape = RoundedCornerShape(12.dp)
    val CardShape = RoundedCornerShape(12.dp)
    val TextFieldShape = RoundedCornerShape(12.dp)
    val ChipShape = RoundedCornerShape(8.dp)
    val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val DialogShape = RoundedCornerShape(16.dp)
    val ImageShape = RoundedCornerShape(12.dp)
}
```

---

## 三、品牌组件设计（融合mx.md装饰元素）

### 3.1 装饰系统定义（新增）

```kotlin
// Decoration.kt - 品牌装饰系统
sealed class CardDecoration {
    object None : CardDecoration()
    
    // 🌟 顶部装饰渐变条
    data class GradientTop(
        val brush: Brush,
        val height: Dp = 4.dp
    ) : CardDecoration()
    
    // 🌟 角落装饰圆点
    data class AccentCorner(
        val color: Color,
        val size: Dp = 8.dp,
        val position: CornerPosition = CornerPosition.TopRight
    ) : CardDecoration()
    
    // 🌟 左侧强调条
    data class LeftAccentBar(
        val color: Color,
        val width: Dp = 4.dp
    ) : CardDecoration()
    
    enum class CornerPosition {
        TopLeft, TopRight, BottomLeft, BottomRight
    }
}

// 🌟 背景装饰元素（新增）
sealed class BackgroundDecoration {
    // 装饰性圆形
    data class Circle(
        val size: Dp,
        val offsetX: Dp,
        val offsetY: Dp,
        val alpha: Float = 0.1f,
        val color: Color = Color.White
    ) : BackgroundDecoration()
    
    // 品牌文字水印
    data class BrandWatermark(
        val text: String,
        val alpha: Float = 0.05f,
        val position: Alignment = Alignment.BottomEnd
    ) : BackgroundDecoration()
    
    // 倾斜装饰方块
    data class TiltedBlock(
        val size: Dp,
        val offsetX: Dp,
        val offsetY: Dp,
        val rotation: Float = 15f,
        val alpha: Float = 0.1f
    ) : BackgroundDecoration()
}
```

### 3.2 品牌按钮（融合涟漪效果）

```kotlin
// Button.kt - 品牌按钮组件
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    buttonType: ButtonType = ButtonType.Primary
) {
    val backgroundColor = when (buttonType) {
        ButtonType.Primary -> AppColors.Primary
        ButtonType.Secondary -> AppColors.Secondary
        ButtonType.Outlined -> Color.Transparent
        ButtonType.Text -> Color.Transparent
        ButtonType.Gradient -> Color.Transparent
    }
    
    val contentColor = when (buttonType) {
        ButtonType.Primary, ButtonType.Secondary, ButtonType.Gradient -> Color.White
        ButtonType.Outlined, ButtonType.Text -> AppColors.Primary
    }
    
    // 🌟 涟漪效果颜色定制（融合mx.md）
    val rippleColor = when (buttonType) {
        ButtonType.Primary, ButtonType.Gradient -> Color.White.copy(alpha = 0.3f)
        else -> AppColors.Primary.copy(alpha = 0.2f)
    }
    
    Button(
        onClick = onClick,
        modifier = modifier.height(AppDimens.ButtonHeight),
        enabled = enabled && !loading,
        shape = AppShapes.ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.38f),
            disabledContentColor = contentColor.copy(alpha = 0.38f)
        ),
        // 🌟 更明显的按压阴影（融合mx.md）
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// 🌟 渐变按钮（新增）
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = AppColors.GradientPrimary,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(AppDimens.ButtonHeight)
            .clip(AppShapes.ButtonShape)
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(gradientColors)
                } else {
                    Brush.horizontalGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.38f) }
                    )
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}

enum class ButtonType {
    Primary, Secondary, Outlined, Text, Gradient
}
```

### 3.3 品牌卡片（融合装饰元素）

```kotlin
// Card.kt - 品牌卡片组件（融合mx.md装饰元素）
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = AppDimens.CardElevation,
    shape: Shape = AppShapes.CardShape,
    backgroundColor: Color = AppColors.Surface,
    // 🌟 装饰元素参数（融合mx.md）
    decoration: CardDecoration = CardDecoration.None,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = AppDimens.CardElevationRaised
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(1.dp, AppColors.Outline.copy(alpha = 0.3f))
    ) {
        Column {
            // 🌟 装饰元素渲染（融合mx.md）
            when (decoration) {
                is CardDecoration.GradientTop -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(decoration.height)
                            .background(decoration.brush)
                    )
                }
                is CardDecoration.AccentCorner -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = when (decoration.position) {
                            CardDecoration.CornerPosition.TopRight -> Alignment.TopEnd
                            CardDecoration.CornerPosition.TopLeft -> Alignment.TopStart
                            CardDecoration.CornerPosition.BottomRight -> Alignment.BottomEnd
                            CardDecoration.CornerPosition.BottomLeft -> Alignment.BottomStart
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = if (decoration.position.name.contains("Right")) decoration.size else -decoration.size,
                                    y = -decoration.size / 2
                                )
                                .size(decoration.size)
                                .background(
                                    color = decoration.color,
                                    shape = AppShapes.Circular
                                )
                        )
                    }
                }
                is CardDecoration.LeftAccentBar -> {
                    Row {
                        Box(
                            modifier = Modifier
                                .width(decoration.width)
                                .fillMaxHeight()
                                .background(decoration.color)
                        )
                    }
                }
                CardDecoration.None -> {}
            }
            
            Column(
                modifier = Modifier.padding(AppDimens.CardPadding),
                content = content
            )
        }
    }
}

// 🌟 渐变卡片（融合mx.md）
@Composable
fun GradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    // 🌟 倾斜装饰元素（融合mx.md）
    showTiltedDecoration: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = AppShapes.Large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = AppShapes.Large
                )
        ) {
            // 🌟 倾斜装饰元素（融合mx.md）
            if (showTiltedDecoration) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .offset(x = 100.dp, y = (-20).dp)
                        .graphicsLayer { rotationZ = 15f }
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = AppShapes.Medium
                        )
                )
            }
            
            Column(
                modifier = Modifier.padding(AppDimens.CardPadding),
                content = content
            )
        }
    }
}

// 🌟 可展开卡片（融合装饰条）
@Composable
fun ExpandableCard(
    title: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    modifier: Modifier = Modifier,
    // 🌟 状态颜色用于装饰条
    accentColor: Color = AppColors.Primary
) {
    var scale by remember { mutableStateOf(1f) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        scale = 0.98f
                        tryAwaitRelease()
                        scale = 1f
                    },
                    onTap = { onExpandToggle() }
                )
            },
        shape = AppShapes.Large,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 2.dp,
            pressedElevation = 12.dp
        )
    ) {
        Column {
            // 🌟 顶部装饰渐变条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accentColor)
            )
            
            Column(modifier = Modifier.padding(AppDimens.CardPadding)) {
                title()
                
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(AppDimens.SpacingSm))
                        Divider(color = AppColors.Outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(AppDimens.SpacingSm))
                        expandedContent()
                    }
                }
            }
        }
    }
}
```

### 3.4 品牌输入框

```kotlin
// TextField.kt - 品牌输入框组件
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = AppShapes.TextFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = AppColors.Outline,
                errorBorderColor = AppColors.Error,
                focusedLabelColor = AppColors.Primary,
                unfocusedLabelColor = AppColors.OnSurfaceVariant,
                errorLabelColor = AppColors.Error,
                cursorColor = AppColors.Primary,
                focusedContainerColor = AppColors.Surface,
                unfocusedContainerColor = AppColors.SurfaceVariant.copy(alpha = 0.5f)
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = AppColors.Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
```

### 3.5 状态芯片

```kotlin
// Chip.kt - 品牌状态芯片
@Composable
fun StatusChip(
    text: String,
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = when (status) {
        StatusType.SUCCESS -> Pair(
            AppColors.SuccessLight,
            AppColors.Success
        )
        StatusType.WARNING -> Pair(
            AppColors.WarningLight,
            Color(0xFFE65100)
        )
        StatusType.ERROR -> Pair(
            AppColors.ErrorLight,
            AppColors.Error
        )
        StatusType.INFO -> Pair(
            AppColors.InfoLight,
            AppColors.Info
        )
        StatusType.PENDING -> Pair(
            AppColors.SecondaryContainerLight,
            AppColors.SecondaryDark
        )
        StatusType.ACTIVE -> Pair(
            AppColors.SuccessLight,
            AppColors.Success
        )
        StatusType.ENDED -> Pair(
            AppColors.SurfaceVariant,
            AppColors.OnSurfaceVariant
        )
    }
    
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = AppShapes.ChipShape
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

enum class StatusType {
    SUCCESS, WARNING, ERROR, INFO, PENDING, ACTIVE, ENDED
}
```

### 3.6 品牌加载组件（融合斜向光效）

```kotlin
// Loading.kt - 品牌加载组件（融合mx.md斜向光效）
@Composable
fun AnimatedLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 外圈
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = AppColors.Primary,
            strokeWidth = 4.dp
        )
        // 内圈动画点
        Box(
            modifier = Modifier
                .size(size * 0.4f * scale)
                .clip(CircleShape)
                .background(AppColors.Secondary)
        )
    }
}

// 🌟 骨架屏组件（融合mx.md斜向光效）
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    type: ShimmerType = ShimmerType.Card
) {
    val shimmerColors = listOf(
        AppColors.SurfaceVariant.copy(alpha = 0.3f),
        AppColors.SurfaceVariant.copy(alpha = 0.5f),
        AppColors.SurfaceVariant.copy(alpha = 0.3f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (type) {
                    ShimmerType.Card -> 1500
                    ShimmerType.ListItem -> 1000
                    ShimmerType.Button -> 800
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    // 🌟 斜向光效（融合mx.md）
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200, 0f),
        end = Offset(translateAnim, 400f) // 斜向角度
    )
    
    Card(
        modifier = modifier,
        shape = AppShapes.Large
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}

enum class ShimmerType {
    Card, ListItem, Button
}

// 🌟 列表项骨架屏（新增）
@Composable
fun ShimmerListItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppDimens.SpacingMd),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerCard(
            modifier = Modifier.size(56.dp),
            type = ShimmerType.ListItem
        )
        
        Spacer(modifier = Modifier.width(AppDimens.SpacingMd))
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingXs)
        ) {
            ShimmerCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                type = ShimmerType.ListItem
            )
            ShimmerCard(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp),
                type = ShimmerType.ListItem
            )
        }
    }
}
```

### 3.7 空状态组件

```kotlin
// EmptyState.kt - 空状态组件
@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppDimens.SpacingXxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        
        Spacer(modifier = Modifier.height(AppDimens.SpacingLg))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.OnSurface
        )
        
        Spacer(modifier = Modifier.height(AppDimens.SpacingSm))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (action != null) {
            Spacer(modifier = Modifier.height(AppDimens.SpacingLg))
            action()
        }
    }
}
```

---

## 四、页面级优化方案（品牌装饰增强版）

### 4.1 登录页优化（融合装饰元素）

```kotlin
// LoginScreen.kt - 品牌装饰增强版
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandGradients.LoginBackgroundGradient)
    ) {
        // 🌟 装饰性圆形背景（融合mx.md）
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .graphicsLayer { alpha = 0.1f }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        // 🌟 第二个装饰圆形（新增）
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = 100.dp)
                .graphicsLayer { alpha = 0.08f }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        
        // 🌟 品牌文字背景水印（融合mx.md）
        Text(
            text = "FRUIT",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White.copy(alpha = 0.06f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimens.ScreenPaddingLarge)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo区域
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color.White,
                        shape = AppShapes.Circular
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = AppColors.Primary
                )
            }
            
            Spacer(modifier = Modifier.height(AppDimens.SpacingLg))
            
            Text(
                text = "果评赛事",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            
            Text(
                text = "水果评价赛事管理系统",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(AppDimens.SpacingXxl))
            
            // 🌟 表单卡片（带装饰）
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.XLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    // 🌟 顶部装饰渐变条
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(BrandGradients.PrimaryGradient)
                    )
                    
                    Column(
                        modifier = Modifier.padding(AppDimens.SpacingLg)
                    ) {
                        Text(
                            text = "欢迎回来",
                            style = MaterialTheme.typography.titleLarge,
                            color = AppColors.OnSurface
                        )
                        
                        Text(
                            text = "请登录您的账号",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.OnSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(AppDimens.SpacingLg))
                        
                        // 用户名输入
                        AppTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "用户名",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = AppColors.Primary
                                )
                            },
                            isError = loginState.error?.contains("username") == true,
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(AppDimens.SpacingMd))
                        
                        // 密码输入
                        AppTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "密码",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = AppColors.Primary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) 
                                            Icons.Default.Visibility 
                                        else 
                                            Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                                        tint = AppColors.OnSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            isError = loginState.error?.contains("password") == true,
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(AppDimens.SpacingSm))
                        
                        // 忘记密码
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { /* 忘记密码逻辑 */ }) {
                                Text(
                                    text = "忘记密码？",
                                    color = AppColors.Primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(AppDimens.SpacingLg))
                        
                        // 🌟 渐变登录按钮
                        GradientButton(
                            text = "登录",
                            onClick = { viewModel.login(username.trim(), password) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = username.isNotBlank() && password.isNotBlank()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(AppDimens.SpacingLg))
            
            // 注册入口
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "还没有账号？",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "立即注册",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
```

### 4.2 主页优化（品牌卡片）

```kotlin
// MainScreen.kt - 品牌装饰增强版
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToCompetition: () -> Unit,
    onNavigateToFruitNutrition: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val username = viewModel.getCurrentUsername() ?: "用户"
    val isAdmin = viewModel.isCurrentUserAdmin()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // 🌟 顶部用户信息卡片（渐变背景）
        GradientCard(
            gradientColors = AppColors.GradientPrimary,
            modifier = Modifier.padding(AppDimens.SpacingMd),
            showTiltedDecoration = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 用户头像
                Surface(
                    modifier = Modifier.size(AppDimens.AvatarSizeMedium),
                    shape = AppShapes.Circular,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(AppDimens.SpacingSm)
                            .size(AppDimens.IconSizeLarge),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(AppDimens.SpacingMd))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "欢迎，$username！",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    if (isAdmin) {
                        StatusChip(
                            text = "管理员",
                            status = StatusType.INFO
                        )
                    }
                }
                
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "退出登录",
                        tint = Color.White
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(AppDimens.SpacingSm))
        
        // 功能入口标题
        Text(
            text = "功能入口",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.OnBackground,
            modifier = Modifier.padding(
                horizontal = AppDimens.SpacingMd,
                vertical = AppDimens.SpacingSm
            )
        )
        
        // 🌟 功能卡片网格（带品牌装饰）
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppDimens.SpacingMd),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingMd)
        ) {
            item {
                BrandFeatureCard(
                    title = "赛事评价",
                    description = "浏览和参与水果评价赛事",
                    icon = Icons.Default.EmojiEvents,
                    gradientColors = AppColors.GradientPrimary,
                    onClick = onNavigateToCompetition
                )
            }
            
            item {
                BrandFeatureCard(
                    title = "水果营养查询",
                    description = "了解各类水果的营养成分",
                    icon = Icons.Default.Restaurant,
                    gradientColors = AppColors.GradientSecondary,
                    onClick = onNavigateToFruitNutrition
                )
            }
            
            if (isAdmin) {
                item {
                    BrandFeatureCard(
                        title = "管理员面板",
                        description = "管理赛事、模型和参赛作品",
                        icon = Icons.Default.AdminPanelSettings,
                        gradientColors = listOf(
                            AppColors.AccentPink,
                            AppColors.AccentPink.copy(alpha = 0.7f)
                        ),
                        onClick = onNavigateToAdmin
                    )
                }
            }
        }
    }
}

// 🌟 品牌功能卡片（带倾斜装饰）
@Composable
private fun BrandFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.Large,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = AppShapes.Large
                )
        ) {
            // 🌟 倾斜装饰元素（融合mx.md）
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 100.dp, y = (-20).dp)
                    .graphicsLayer { rotationZ = 15f }
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = AppShapes.Medium
                    )
            )
            
            // 🌟 角落装饰圆点（新增）
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(12.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = AppShapes.Circular
                    )
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimens.CardPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(AppDimens.IconSizeXLarge),
                    shape = AppShapes.Medium,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(AppDimens.SpacingSm),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(AppDimens.SpacingMd))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
```

### 4.3 赛事列表优化（装饰条增强）

```kotlin
// CompetitionScreen.kt - 品牌装饰增强版
@Composable
fun CompetitionScreen(
    onBack: () -> Unit,
    onCompetitionClick: (Long) -> Unit,
    onNavigateToDataDisplay: (Long) -> Unit,
    onNavigateToEntryAdd: (Long, String) -> Unit,
    viewModel: CompetitionViewModel = hiltViewModel()
) {
    val competitions by viewModel.competitions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "赛事评价",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    // 加载骨架屏
                    LazyColumn(
                        contentPadding = PaddingValues(AppDimens.SpacingMd),
                        verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingMd)
                    ) {
                        items(3) {
                            ShimmerCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                type = ShimmerType.Card
                            )
                        }
                    }
                }
                
                errorMessage != null -> {
                    EmptyState(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = AppColors.Error
                            )
                        },
                        title = "加载失败",
                        description = errorMessage ?: "未知错误",
                        action = {
                            AppButton(
                                text = "重试",
                                onClick = { viewModel.refresh() }
                            )
                        }
                    )
                }
                
                competitions.isEmpty() -> {
                    EmptyState(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = AppColors.OnSurfaceVariant
                            )
                        },
                        title = "暂无赛事",
                        description = "当前没有进行中的评价赛事，请稍后再试或联系管理员",
                        action = {
                            AppButton(
                                text = "刷新",
                                onClick = { viewModel.refresh() },
                                buttonType = ButtonType.Outlined
                            )
                        }
                    )
                }
                
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(AppDimens.SpacingMd),
                        verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingMd)
                    ) {
                        items(competitions) { competition ->
                            BrandCompetitionCard(
                                competition = competition,
                                onClick = { onCompetitionClick(competition.id) },
                                onNavigateToDataDisplay = { 
                                    onNavigateToDataDisplay(competition.id) 
                                },
                                onNavigateToEntryAdd = { id, name -> 
                                    onNavigateToEntryAdd(id, name) 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 🌟 品牌赛事卡片（带状态颜色装饰条）
@Composable
private fun BrandCompetitionCard(
    competition: Competition,
    onClick: () -> Unit,
    onNavigateToDataDisplay: () -> Unit,
    onNavigateToEntryAdd: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    // 🌟 根据状态选择装饰色
    val accentColor = when (competition.status) {
        "进行中" -> AppColors.Success
        "待开始" -> AppColors.Warning
        "已结束" -> AppColors.OnSurfaceVariant
        else -> AppColors.Primary
    }
    
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.Large,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (expanded) 4.dp else 2.dp
        )
    ) {
        Column {
            // 🌟 顶部状态装饰条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accentColor)
            )
            
            Column(
                modifier = Modifier.padding(AppDimens.CardPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // 赛事图标
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = AppShapes.Medium,
                        color = accentColor.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = accentColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(AppDimens.SpacingMd))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = competition.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(AppDimens.SpacingXs))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = AppColors.OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "截止: ${competition.deadline}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.OnSurfaceVariant
                            )
                        }
                    }
                    
                    // 状态标签
                    StatusChip(
                        text = competition.status,
                        status = when (competition.status) {
                            "进行中" -> StatusType.ACTIVE
                            "待开始" -> StatusType.PENDING
                            "已结束" -> StatusType.ENDED
                            else -> StatusType.INFO
                        }
                    )
                }
                
                // 🌟 展开的详情区域（带动画）
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(AppDimens.SpacingMd))
                        Divider(color = AppColors.Outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(AppDimens.SpacingMd))
                        
                        // 快捷操作按钮组
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconActionButton(
                                icon = Icons.Default.Assessment,
                                label = "数据",
                                onClick = onNavigateToDataDisplay
                            )
                            IconActionButton(
                                icon = Icons.Default.RateReview,
                                label = "评分",
                                onClick = onClick
                            )
                            IconActionButton(
                                icon = Icons.Default.Add,
                                label = "提交",
                                onClick = onNavigateToEntryAdd
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = AppShapes.Medium,
            color = AppColors.Primary.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(12.dp),
                tint = AppColors.Primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AppColors.OnSurfaceVariant
        )
    }
}
```

---

## 五、动效规范

### 5.1 页面过渡动画
```kotlin
// Navigation.kt - 导航过渡动画
val navigationTransitions = object : NavHostAnimatedVisibilityScope {
    override fun AnimatedContentScope.enterTransition(): EnterTransition {
        return fadeIn(animationSpec = tween(300)) +
               slideInHorizontally(
                   initialOffsetX = { it },
                   animationSpec = tween(300, easing = FastOutSlowInEasing)
               )
    }
    
    override fun AnimatedContentScope.exitTransition(): ExitTransition {
        return fadeOut(animationSpec = tween(300)) +
               slideOutHorizontally(
                   targetOffsetX = { -it / 3 },
                   animationSpec = tween(300, easing = FastOutSlowInEasing)
               )
    }
}
```

### 5.2 组件动效规范
| 场景 | 动画类型 | 持续时间 | 缓动函数 |
|------|----------|----------|----------|
| 按钮点击 | Scale + Alpha | 150ms | FastOutSlowIn |
| 卡片展开 | Expand/Fade | 300ms | FastOutSlowIn |
| 页面加载 | Fade + Slide | 400ms | FastOutSlowIn |
| 列表项进入 | Stagger Fade | 50ms/item | Linear |
| 数值变化 | Count Up | 600ms | FastOutSlowIn |
| 骨架屏shimmer | 斜向光效 | 1000-1500ms | Linear |

### 5.3 微交互动画
```kotlin
// 🌟 点击缩放效果
fun Modifier.clickableScale(
    scale: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) scale else 1f,
        animationSpec = tween(100),
        label = "clickScale"
    )
    
    this
        .scale(animatedScale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = { onClick() }
            )
        }
}
```

---

## 六、实施建议

### 6.1 实施优先级

**P0 - 基础样式（必做）**
- [ ] 重构Color.kt - 建立新的色彩系统（包含品牌渐变）
- [ ] 重构Type.kt - 建立新的排版系统（Noto Sans SC）
- [ ] 重构Theme.kt - 整合新的色彩和字体
- [ ] 创建Dimens.kt - 统一间距和尺寸系统
- [ ] 创建Shape.kt - 定义圆角系统

**P1 - 装饰系统（核心差异化）**
- [ ] 创建Decoration.kt - 定义品牌装饰系统
- [ ] 创建Button.kt - 品牌按钮（含渐变按钮）
- [ ] 创建Card.kt - 品牌卡片（含装饰元素）
- [ ] 创建Loading.kt - 品牌加载（含斜向shimmer）

**P2 - 核心页面（重要）**
- [ ] 优化登录页（品牌装饰背景）
- [ ] 优化主页（品牌功能卡片）
- [ ] 优化赛事列表（状态装饰条）

**P3 - 业务页面（次要）**
- [ ] 优化赛事相关页面
- [ ] 优化水果查询页面
- [ ] 优化管理员页面

**P4 - 增强体验（可选）**
- [ ] 添加动效系统
- [ ] 完善暗色模式
- [ ] 添加微交互

### 6.2 文件修改清单

| 优先级 | 文件路径 | 修改类型 | 修改内容 |
|--------|----------|----------|----------|
| P0 | ui/theme/Color.kt | 重写 | 全新色彩系统 + 品牌渐变 |
| P0 | ui/theme/Type.kt | 重写 | 完整排版系统（Noto Sans SC） |
| P0 | ui/theme/Theme.kt | 修改 | 应用新主题 |
| P0 | ui/theme/Dimens.kt | 新建 | 间距、尺寸、圆角系统 |
| P0 | ui/theme/Shape.kt | 新建 | 形状系统 |
| P1 | ui/components/Decoration.kt | 新建 | 品牌装饰系统 |
| P1 | ui/components/Buttons.kt | 新建 | 品牌按钮（含渐变按钮） |
| P1 | ui/components/Cards.kt | 新建 | 品牌卡片（含装饰） |
| P1 | ui/components/LoadingStates.kt | 新建 | 品牌加载（含斜向shimmer） |
| P1 | ui/components/Chips.kt | 新建 | 状态芯片组件 |
| P1 | ui/components/EmptyStates.kt | 新建 | 空状态组件 |
| P2 | ui/screen/LoginScreen.kt | 重写 | 品牌装饰增强版 |
| P2 | ui/screen/MainScreen.kt | 重写 | 品牌功能卡片 |
| P2 | ui/screen/CompetitionScreen.kt | 修改 | 状态装饰条增强 |
| P3 | ui/screen/其他页面 | 修改 | 应用品牌组件 |

### 6.3 依赖库建议
```gradle
// 在 build.gradle.kts 中添加
dependencies {
    // 图片加载（用于水果图标）
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Lottie动画（可选，用于成功动画）
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    
    // Accompanist动画库
    implementation("com.google.accompanist:accompanist-navigation-animation:0.32.0")
}
```

### 6.4 字体文件清单
```
android-app/app/src/main/res/font/
├── noto_sans_sc_regular.ttf
├── noto_sans_sc_medium.ttf
└── noto_sans_sc_bold.ttf
```

---

## 七、预期效果

### 7.1 视觉提升
- ✅ 自然绿+暖橙配色与产品定位完美契合
- ✅ 品牌装饰元素（渐变条、角落装饰、倾斜方块）增强辨识度
- ✅ 中文Noto Sans SC字体优化阅读体验
- ✅ 状态颜色装饰条直观展示赛事状态

### 7.2 交互提升
- ✅ 渐变按钮和卡片提升视觉层次
- ✅ 斜向shimmer效果增加精致感
- ✅ 涟漪效果颜色与品牌色协调
- ✅ 页面过渡动画流畅自然

### 7.3 品牌识别度提升
- ✅ 登录页装饰圆形+品牌水印建立第一印象
- ✅ 功能卡片倾斜装饰元素形成视觉记忆
- ✅ 统一的品牌色彩系统贯穿全应用
- ✅ 与竞品形成明显的视觉差异化

---

## 八、注意事项

1. **性能考虑**: 装饰元素使用`graphicsLayer`确保硬件加速，避免过度绘制
2. **向后兼容**: 所有修改保持原有功能逻辑不变，仅修改UI表现
3. **无障碍**: 确保装饰元素不影响屏幕阅读器识别主要内容
4. **渐进式实施**: 建议按P0-P3优先级分阶段实施，便于验证效果

---

## 九、参考资源

- [Material Design 3 官方文档](https://m3.material.io/)
- [Jetpack Compose 动画指南](https://developer.android.com/jetpack/compose/animation)
- [Noto Sans SC 字体](https://fonts.google.com/noto/specimen/Noto+Sans+SC)

---

**文档版本**: v3.0 - 品牌装饰增强版  
**基于**: km.md (v2.0) + mx.md 装饰元素  
**创建日期**: 2026年2月9日  
**优化重点**: 在km.md自然清新基础上，融合mx.md品牌装饰元素，提升品牌辨识度
