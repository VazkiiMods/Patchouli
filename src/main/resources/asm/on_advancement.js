function injectForEachInsn(method, opcode, callback) {
    var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');

    var target = ASM.findFirstInstruction(method,
        opcode);

    while (target !== null) {
        var index = method.instructions.indexOf(target);
        var indexShift = callback(target, index);

        var newIndex = method.instructions.indexOf(target);
        if (newIndex !== -1)
            index = newIndex;
        else if (typeof indexShift === 'number')
            index += indexShift;

        target = ASM.findFirstInstructionAfter(method,
            opcode,
            index + 1);
    }

    return method;
}

function initializeCoreMod() {
    return {
        'on_advancement': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.multiplayer.ClientAdvancementManager',
                'methodName': 'func_192799_a',
                'methodDesc': '(Lnet/minecraft/network/play/server/SAdvancementInfoPacket;)V'
            },
            'transformer': function(method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');

                return injectForEachInsn(method, Opcodes.RETURN, function (target) {
                    var newInsns = new InsnList();
                    newInsns.add(ASM.buildMethodCall(
                        "vazkii/patchouli/client/base/ClientAdvancements",
                        "onClientPacket",
                        "()V",
                        ASM.MethodType.STATIC
                    ));
                    method.instructions.insertBefore(target, newInsns);
                });
            }
        }
    }
}